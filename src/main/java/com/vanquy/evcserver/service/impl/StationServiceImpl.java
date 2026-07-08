package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.dto.response.*;
import com.vanquy.evcserver.exception.BadRequestException;
import com.vanquy.evcserver.exception.ResourceNotFoundException;
import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.ConnectorType;
import com.vanquy.evcserver.model.StationCheckin;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.ChargingStationRepository;
import com.vanquy.evcserver.repository.ConnectorTypeRepository;
import com.vanquy.evcserver.repository.StationCheckinRepository;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.service.StationService;
import com.vanquy.evcserver.util.ImageUtil;
import com.vanquy.evcserver.util.PopularTimesUtil;
import com.vanquy.evcserver.util.TopsisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final ChargingStationRepository stationRepository;
    private final ConnectorTypeRepository connectorTypeRepository;
    private final StationCheckinRepository checkinRepository;
    private final UserRepository userRepository;

    /** Số điểm thưởng cộng thêm cho mỗi lần check-in hợp lệ */
    private static final int REWARD_POINTS_PER_CHECKIN = 10;

    /** Thời gian tối thiểu giữa 2 lần check-in cùng trạm (phút) */
    private static final int CHECKIN_COOLDOWN_MINUTES = 10;

    // ═══════════════════════════════════════════════════
    // TÌM KIẾM TRẠM SẠC (có hỗ trợ TOPSIS)
    // ═══════════════════════════════════════════════════

    @Override
    public List<StationSummaryResponse> searchStations(
            double latitude, double longitude,
            double radius, String connectorType,
            Integer minPowerKw, Integer maxPowerKw,
            Double minRating,
            boolean useTopsis,
            double weightDistance, double weightPower,
            double weightOccupancy, double weightRating,
            Double userLatitude, Double userLongitude
    ) {
        //Bounding Box
        double deltaLat = radius / 111.12;
        double deltaLng = radius / (111.12 * Math.cos(Math.toRadians(latitude)));

        String normalizedConnectorType = (connectorType != null && !connectorType.isBlank())
                ? connectorType : null;

        List<Object[]> results = stationRepository.findNearbyStationsFiltered(
                latitude, longitude, radius, normalizedConnectorType,
                minPowerKw, maxPowerKw, minRating,
                latitude - deltaLat, latitude + deltaLat,
                longitude - deltaLng, longitude + deltaLng
        );

        //Map sang DTO
        List<StationSummaryResponse> stations = results.stream()
                .map(this::mapToStationSummary)
                .collect(Collectors.toList());

        //Truy vấn batch trạng thái check-in mới nhất cho tất cả các trạm
        if (!stations.isEmpty()) {
            List<Long> stationIds = stations.stream()
                    .map(StationSummaryResponse::getStationId)
                    .toList();

            Map<Long, StationCheckin> latestCheckins = getLatestCheckinsMap(stationIds);

            for (StationSummaryResponse s : stations) {
                StationCheckin checkin = latestCheckins.get(s.getStationId());
                if (checkin != null) {
                    s.setCrowdStatus(checkin.getStatus());
                    s.setStatusUpdatedAt(formatTimeAgo(checkin.getCreatedAt()));
                    s.setStatusUpdatedByName(checkin.getUser().getFullName());
                } else {
                    s.setCrowdStatus(null);
                }
            }
        }

        // Tính toán lại khoảng cách địa lý thực tế tương đối với vị trí thực của người dùng (nếu có)
        if (userLatitude != null && userLongitude != null && !stations.isEmpty()) {
            for (StationSummaryResponse s : stations) {
                double dist = calculateHaversineDistance(
                        userLatitude, userLongitude,
                        s.getLatitude().doubleValue(), s.getLongitude().doubleValue()
                );
                s.setDistance(Math.round(dist * 10.0) / 10.0);
            }
            if (!useTopsis) {
                stations.sort((a, b) -> Double.compare(
                        a.getDistance() != null ? a.getDistance() : 0.0,
                        b.getDistance() != null ? b.getDistance() : 0.0
                ));
            }
        }

        //Nếu bật TOPSIS => tính Match Score, sắp xếp theo điểm giảm dần và lọc ra các trạm phù hợp nhất
        if (useTopsis && !stations.isEmpty()) {
            TopsisUtil.calculateMatchScores(
                    stations, weightDistance, weightPower, weightOccupancy, weightRating
            );
            stations.sort((a, b) -> Integer.compare(
                    b.getMatchScore() != null ? b.getMatchScore() : 0,
                    a.getMatchScore() != null ? a.getMatchScore() : 0
            ));

            // Chỉ hiển thị các trạm sạc phù hợp nhất (Match Score >= 50%), luôn giữ lại ít nhất 5 trạm tốt nhất
            int minKeep = Math.min(5, stations.size());
            List<StationSummaryResponse> filteredStations = new java.util.ArrayList<>();
            for (int i = 0; i < stations.size(); i++) {
                StationSummaryResponse s = stations.get(i);
                if (i < minKeep || (s.getMatchScore() != null && s.getMatchScore() >= 50)) {
                    filteredStations.add(s);
                }
            }
            stations = filteredStations;
        }

        return stations;
    }

    // ═══════════════════════════════════════════════════
    // CHI TIẾT TRẠM SẠC (kèm Popular Times + Crowd Status)
    // ═══════════════════════════════════════════════════

    @Override
    public StationDetailResponse getStationDetail(Long stationId) {
        ChargingStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Trạm sạc", "stationId", stationId));

        List<ConnectorTypeResponse> connectors = station.getConnectorTypes().stream()
                .map(this::mapToConnectorResponse)
                .toList();

        StationDetailResponse.StationDetailResponseBuilder builder = StationDetailResponse.builder()
                .stationId(station.getStationId())
                .name(station.getName())
                .address(station.getAddress())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .openingHours(station.getOpeningHours())
                .imageUrl(ImageUtil.getStationImageUrl(station.getImageUrl()))
                .rating(station.getRating())
                .totalReviews(station.getTotalReviews())
                .isActive(station.getIsActive())
                .connectorTypes(connectors);

        // Truy vấn trạng thái check-in mới nhất
        Optional<StationCheckin> latestCheckin =
                checkinRepository.findFirstByStationStationIdOrderByCreatedAtDesc(stationId);

        if (latestCheckin.isPresent()) {
            StationCheckin c = latestCheckin.get();
            builder.crowdStatus(c.getStatus());
            builder.statusUpdatedAt(formatTimeAgo(c.getCreatedAt()));
            builder.statusUpdatedByName(c.getUser().getFullName());
        } else {
            builder.crowdStatus(null);
        }

        // Popular Times (dữ liệu 7 ngày x 24 giờ - lai trộn check-in thực tế)
        List<Object[]> checkinCounts = checkinRepository.countCheckinsGroupByDayAndHour(stationId);
        builder.popularTimes(PopularTimesUtil.getWeeklyPopularTimes(stationId, checkinCounts));

        return builder.build();
    }

    // ═══════════════════════════════════════════════════
    // CHECK-IN & BÁO CÁO TRẠNG THÁI
    // ═══════════════════════════════════════════════════

    @Override
    @Transactional
    public void checkin(Long userId, Long stationId, String status, String imageUrl) {
        //Validate trạng thái
        if (!Set.of("EMPTY", "MODERATE", "BUSY", "MAINTENANCE").contains(status)) {
            throw new BadRequestException(
                    "Trạng thái không hợp lệ. Chỉ chấp nhận: EMPTY, MODERATE, BUSY, MAINTENANCE");
        }

        //Kiểm tra trạm sạc tồn tại
        ChargingStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Trạm sạc", "stationId", stationId));

        //Kiểm tra người dùng tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "userId", userId));

        //Kiểm tra thời gian check-in gần nhất
        LocalDateTime cooldownTime = LocalDateTime.now().minusMinutes(CHECKIN_COOLDOWN_MINUTES);
        Optional<StationCheckin> recentCheckin =
                checkinRepository.findFirstByUserUserIdAndStationStationIdAndCreatedAtAfterOrderByCreatedAtDesc(
                        userId, stationId, cooldownTime);

        if (recentCheckin.isPresent()) {
            throw new BadRequestException(
                    "Bạn chỉ có thể check-in tại cùng một trạm sạc mỗi " + CHECKIN_COOLDOWN_MINUTES + " phút");
        }

        //Tạo bản ghi check-in mới
        StationCheckin checkin = StationCheckin.builder()
                .user(user)
                .station(station)
                .status(status)
                .imageUrl(com.vanquy.evcserver.util.ImageUtil.sanitizeCheckinImage(imageUrl))
                .build();
        checkinRepository.save(checkin);

        //Cộng điểm thưởng cho người dùng
        int currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
        user.setRewardPoints(currentPoints + REWARD_POINTS_PER_CHECKIN);
        userRepository.save(user);
    }

    // ═══════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════

    /**
     * Map native query result (Object[]) → StationSummaryResponse.
     */
    private StationSummaryResponse mapToStationSummary(Object[] row) {
        Long stationId = ((Number) row[0]).longValue();

        List<ConnectorType> connectors = connectorTypeRepository.findByStationStationId(stationId);
        List<ConnectorTypeResponse> connectorResponses = connectors.stream()
                .map(this::mapToConnectorResponse)
                .toList();

        return StationSummaryResponse.builder()
                .stationId(stationId)
                .name((String) row[1])
                .address((String) row[2])
                .latitude(row[3] instanceof BigDecimal bd ? bd : BigDecimal.valueOf(((Number) row[3]).doubleValue()))
                .longitude(row[4] instanceof BigDecimal bd ? bd : BigDecimal.valueOf(((Number) row[4]).doubleValue()))
                .openingHours((String) row[5])
                .imageUrl(ImageUtil.getStationImageUrl((String) row[6]))
                .rating(row[7] instanceof BigDecimal bd ? bd : BigDecimal.valueOf(((Number) row[7]).doubleValue()))
                .totalReviews(((Number) row[8]).intValue())
                .distance(Math.round(((Number) row[row.length - 1]).doubleValue() * 10.0) / 10.0)
                .connectorTypes(connectorResponses)
                .build();
    }

    private ConnectorTypeResponse mapToConnectorResponse(ConnectorType ct) {
        return ConnectorTypeResponse.builder()
                .type(ct.getType())
                .powerKw(ct.getPowerKw())
                .totalPorts(ct.getTotalPorts())
                .build();
    }

    /**
     * Batch truy vấn check-in mới nhất cho danh sách trạm sạc → Map<stationId, StationCheckin>.
     */
    private Map<Long, StationCheckin> getLatestCheckinsMap(List<Long> stationIds) {
        List<StationCheckin> latestCheckins = checkinRepository.findLatestCheckinsByStationIds(stationIds);
        Map<Long, StationCheckin> map = new HashMap<>();
        for (StationCheckin c : latestCheckins) {
            map.put(c.getStation().getStationId(), c);
        }
        return map;
    }

    /**
     * Định dạng thời gian 
     */
    private String formatTimeAgo(LocalDateTime time) {
        if (time == null) return null;
        Duration duration = Duration.between(time, LocalDateTime.now());
        long minutes = duration.toMinutes();
        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";
        long hours = duration.toHours();
        if (hours < 24) return hours + " giờ trước";
        long days = duration.toDays();
        return days + " ngày trước";
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371 * c; // Bán kính Trái Đất theo km
    }
}
