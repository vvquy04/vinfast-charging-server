package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.dto.request.AdminStationRequest;
import com.vanquy.evcserver.dto.response.*;
import com.vanquy.evcserver.exception.ResourceNotFoundException;
import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.ConnectorType;
import com.vanquy.evcserver.model.Review;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.model.StationCheckin;
import com.vanquy.evcserver.repository.*;
import com.vanquy.evcserver.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vanquy.evcserver.util.ImageUtil;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

        private final ChargingStationRepository stationRepository;
        private final ConnectorTypeRepository connectorTypeRepository;
        private final UserRepository userRepository;
        private final ReviewRepository reviewRepository;
        private final UserStationHistoryRepository historyRepository;
        private final StationCheckinRepository checkinRepository;

        // ═══════════════════════════════════════════════════
        // DASHBOARD
        // ═══════════════════════════════════════════════════

        @Override
        public AdminDashboardStatsResponse getDashboardStats() {
                long totalStations = stationRepository.countByIsActive(true);
                long totalUsers = userRepository.count();
                long totalReviews = reviewRepository.count();
                long totalVisits = historyRepository.count();
                long totalCheckins = checkinRepository.count();

                // Thống kê trạm theo quận/huyện (lấy phần trước dấu " - " trong tên trạm)
                List<ChargingStation> allStations = stationRepository.findAll();
                Map<String, Long> districtMap = allStations.stream()
                                .collect(Collectors.groupingBy(
                                                s -> {
                                                        String name = s.getName();
                                                        int idx = name.indexOf(" - ");
                                                        return idx > 0 ? name.substring(0, idx) : "Khác";
                                                },
                                                Collectors.counting()));
                List<AdminDashboardStatsResponse.DistrictStationCount> stationsByDistrict = districtMap.entrySet()
                                .stream()
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .map(e -> AdminDashboardStatsResponse.DistrictStationCount.builder()
                                                .district(e.getKey())
                                                .count(e.getValue())
                                                .build())
                                .toList();

                // Thống kê đánh giá theo tháng (6 tháng gần nhất)
                List<Review> allReviews = reviewRepository.findAll();
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
                Map<String, Long> monthMap = allReviews.stream()
                                .collect(Collectors.groupingBy(
                                                r -> r.getCreatedAt().format(monthFormatter),
                                                TreeMap::new,
                                                Collectors.counting()));
                List<AdminDashboardStatsResponse.MonthlyReviewCount> reviewsByMonth = monthMap.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(e -> AdminDashboardStatsResponse.MonthlyReviewCount.builder()
                                                .month(e.getKey())
                                                .count(e.getValue())
                                                .build())
                                .toList();

                // ─── THỐNG KÊ CHECK-IN ───────────────────────────

                // Bản đồ chuyển đổi mã trạng thái → nhãn tiếng Việt
                Map<String, String> statusLabelMap = Map.of(
                        "EMPTY", "Trống chỗ",
                        "MODERATE", "Vừa phải",
                        "BUSY", "Đang bận / Đầy",
                        "MAINTENANCE", "Bảo trì"
                );

                // 1. Phân bố check-in theo trạng thái
                List<AdminDashboardStatsResponse.CheckinStatusCount> checkinsByStatus =
                        checkinRepository.countCheckinsByStatus().stream()
                                .map(row -> AdminDashboardStatsResponse.CheckinStatusCount.builder()
                                        .status((String) row[0])
                                        .label(statusLabelMap.getOrDefault((String) row[0], (String) row[0]))
                                        .count(((Number) row[1]).longValue())
                                        .build())
                                .toList();

                // 2. Check-in theo tháng
                List<AdminDashboardStatsResponse.MonthlyCheckinCount> checkinsByMonth =
                        checkinRepository.countCheckinsByMonth().stream()
                                .map(row -> AdminDashboardStatsResponse.MonthlyCheckinCount.builder()
                                        .month((String) row[0])
                                        .count(((Number) row[1]).longValue())
                                        .build())
                                .toList();

                // 3. Top 5 trạm sạc được check-in nhiều nhất
                List<AdminDashboardStatsResponse.TopCheckinStation> topCheckinStations =
                        checkinRepository.findTopCheckinStations(5).stream()
                                .map(row -> AdminDashboardStatsResponse.TopCheckinStation.builder()
                                        .stationId(((Number) row[0]).longValue())
                                        .stationName((String) row[1])
                                        .checkinCount(((Number) row[2]).longValue())
                                        .build())
                                .toList();

                return AdminDashboardStatsResponse.builder()
                                .totalStations(totalStations)
                                .totalUsers(totalUsers)
                                .totalReviews(totalReviews)
                                .totalVisits(totalVisits)
                                .totalCheckins(totalCheckins)
                                .stationsByDistrict(stationsByDistrict)
                                .reviewsByMonth(reviewsByMonth)
                                .checkinsByStatus(checkinsByStatus)
                                .checkinsByMonth(checkinsByMonth)
                                .topCheckinStations(topCheckinStations)
                                .build();
        }

        // ═══════════════════════════════════════════════════
        // QUẢN LÝ TRẠM SẠC
        // ═══════════════════════════════════════════════════

        @Override
        public Page<StationDetailResponse> getAllStations(String search, Pageable pageable) {
                List<ChargingStation> allStations = stationRepository.findAll();

                // Lọc theo từ khóa tìm kiếm
                if (search != null && !search.isBlank()) {
                        String lower = search.toLowerCase();
                        allStations = allStations.stream()
                                        .filter(s -> s.getName().toLowerCase().contains(lower)
                                                        || s.getAddress().toLowerCase().contains(lower))
                                        .toList();
                }

                // Phân trang thủ công
                int start = (int) pageable.getOffset();
                int end = Math.min(start + pageable.getPageSize(), allStations.size());
                List<StationDetailResponse> page = (start > allStations.size())
                                ? List.of()
                                : allStations.subList(start, end).stream()
                                                .map(this::toStationDetailResponse)
                                                .toList();

                return new PageImpl<>(page, pageable, allStations.size());
        }

        @Override
        @Transactional
        public StationDetailResponse createStation(AdminStationRequest request) {
                ChargingStation station = ChargingStation.builder()
                                .name(request.getName())
                                .address(request.getAddress())
                                .latitude(BigDecimal.valueOf(request.getLatitude()))
                                .longitude(BigDecimal.valueOf(request.getLongitude()))
                                .openingHours(request.getOpeningHours() != null ? request.getOpeningHours() : "24/7")
                                .imageUrl(ImageUtil.sanitizeStationImage(request.getImageUrl()))
                                .build();

                // Thêm các connector
                if (request.getConnectors() != null) {
                        for (AdminStationRequest.ConnectorInput ci : request.getConnectors()) {
                                ConnectorType ct = ConnectorType.builder()
                                                .type(ci.getType())
                                                .powerKw(ci.getPowerKw())
                                                .totalPorts(ci.getTotalPorts())
                                                .station(station)
                                                .build();
                                station.getConnectorTypes().add(ct);
                        }
                }

                stationRepository.save(station);
                return toStationDetailResponse(station);
        }

        @Override
        @Transactional
        public StationDetailResponse updateStation(Long stationId, AdminStationRequest request) {
                ChargingStation station = stationRepository.findById(stationId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy trạm sạc với ID: " + stationId));

                station.setName(request.getName());
                station.setAddress(request.getAddress());
                station.setLatitude(BigDecimal.valueOf(request.getLatitude()));
                station.setLongitude(BigDecimal.valueOf(request.getLongitude()));
                station.setOpeningHours(request.getOpeningHours() != null ? request.getOpeningHours() : "24/7");
                station.setImageUrl(ImageUtil.sanitizeStationImage(request.getImageUrl()));
                station.setIsActive(true);

                // Xóa connectors cũ và thêm mới
                station.getConnectorTypes().clear();
                if (request.getConnectors() != null) {
                        for (AdminStationRequest.ConnectorInput ci : request.getConnectors()) {
                                ConnectorType ct = ConnectorType.builder()
                                                .type(ci.getType())
                                                .powerKw(ci.getPowerKw())
                                                .totalPorts(ci.getTotalPorts())
                                                .station(station)
                                                .build();
                                station.getConnectorTypes().add(ct);
                        }
                }

                stationRepository.save(station);
                return toStationDetailResponse(station);
        }

        @Override
        @Transactional
        public void deleteStation(Long stationId) {
                ChargingStation station = stationRepository.findById(stationId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy trạm sạc với ID: " + stationId));
                station.setIsActive(false);
                stationRepository.save(station);
        }

        // ═══════════════════════════════════════════════════
        // QUẢN LÝ NGƯỜI DÙNG
        // ═══════════════════════════════════════════════════

        @Override
        public Page<UserProfileResponse> getAllUsers(String search, Pageable pageable) {
                List<User> allUsers = userRepository.findAll();

                if (search != null && !search.isBlank()) {
                        String lower = search.toLowerCase();
                        allUsers = allUsers.stream()
                                        .filter(u -> (u.getFullName() != null
                                                        && u.getFullName().toLowerCase().contains(lower))
                                                        || u.getPhoneNumber().toLowerCase().contains(lower)
                                                        || (u.getEmail() != null
                                                                        && u.getEmail().toLowerCase().contains(lower)))
                                        .toList();
                }

                int start = (int) pageable.getOffset();
                int end = Math.min(start + pageable.getPageSize(), allUsers.size());
                List<UserProfileResponse> page = (start > allUsers.size())
                                ? List.of()
                                : allUsers.subList(start, end).stream()
                                                .map(this::toUserProfileResponse)
                                                .toList();

                return new PageImpl<>(page, pageable, allUsers.size());
        }

        @Override
        @Transactional
        public void toggleUserActive(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy người dùng với ID: " + userId));
                user.setIsActive(!user.getIsActive());
                userRepository.save(user);
        }

        // ═══════════════════════════════════════════════════
        // QUẢN LÝ ĐÁNH GIÁ
        // ═══════════════════════════════════════════════════

        @Override
        public Page<ReviewResponse> getAllReviews(String search, Pageable pageable) {
                Page<Review> reviews = reviewRepository.findAllBySearch(search, pageable);
                List<ReviewResponse> content = reviews.getContent().stream()
                                .map(this::toReviewResponse)
                                .toList();
                return new PageImpl<>(content, pageable, reviews.getTotalElements());
        }

        @Override
        @Transactional
        public void deleteReview(Long reviewId) {
                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy đánh giá với ID: " + reviewId));

                // Cập nhật rating của trạm sau khi xóa review
                ChargingStation station = review.getStation();
                reviewRepository.delete(review);

                // Recalculate rating
                Double avgRating = reviewRepository.getAverageRatingByStationId(station.getStationId());
                Integer totalReviews = reviewRepository.countByStationId(station.getStationId());
                station.setRating(BigDecimal.valueOf(avgRating != null ? avgRating : 0));
                station.setTotalReviews(totalReviews != null ? totalReviews : 0);
                stationRepository.save(station);
        }

        // ═══════════════════════════════════════════════════
        // MAPPER HELPERS
        // ═══════════════════════════════════════════════════

        private StationDetailResponse toStationDetailResponse(ChargingStation station) {
                List<ConnectorTypeResponse> connectors = station.getConnectorTypes().stream()
                                .map(ct -> ConnectorTypeResponse.builder()
                                                .connectorId(ct.getConnectorId())
                                                .type(ct.getType())
                                                .powerKw(ct.getPowerKw())
                                                .totalPorts(ct.getTotalPorts())
                                                .build())
                                .toList();

                return StationDetailResponse.builder()
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
                                .connectorTypes(connectors)
                                .build();
        }

        private UserProfileResponse toUserProfileResponse(User user) {
                return UserProfileResponse.builder()
                                .userId(user.getUserId())
                                .fullName(user.getFullName())
                                .phoneNumber(user.getPhoneNumber())
                                .email(user.getEmail())
                                .gender(user.getGender())
                                .dateOfBirth(user.getDateOfBirth())
                                .avatarUrl(ImageUtil.getAvatarImageUrl(user.getAvatarUrl()))
                                .vehicleModel(user.getVehicleModel())
                                .connectorType(user.getConnectorType())
                                .role(user.getRole())
                                .isActive(user.getIsActive())
                                .createdAt(user.getCreatedAt())
                                .rewardPoints(user.getRewardPoints() != null ? user.getRewardPoints() : 0)
                                .build();
        }

        private ReviewResponse toReviewResponse(Review review) {
                return ReviewResponse.builder()
                                .reviewId(review.getReviewId())
                                .userId(review.getUser().getUserId())
                                .fullName(review.getUser().getFullName())
                                .avatarUrl(ImageUtil.getAvatarImageUrl(review.getUser().getAvatarUrl()))
                                .stationId(review.getStation().getStationId())
                                .stationName(review.getStation().getName())
                                .rating(review.getRating())
                                .comment(review.getComment())
                                .createdAt(review.getCreatedAt())
                                .build();
        }

        @Override
        public Page<CheckinResponse> getAllCheckins(String search, Pageable pageable) {
                List<StationCheckin> allCheckins = checkinRepository.findAll();

                // Sắp xếp mặc định giảm dần theo checkinId
                allCheckins.sort((a, b) -> b.getCheckinId().compareTo(a.getCheckinId()));

                // Lọc theo từ khóa tìm kiếm
                if (search != null && !search.isBlank()) {
                        String lower = search.toLowerCase();
                        allCheckins = allCheckins.stream()
                                        .filter(c -> (c.getUser().getFullName() != null && c.getUser().getFullName().toLowerCase().contains(lower))
                                                || (c.getStation().getName() != null && c.getStation().getName().toLowerCase().contains(lower))
                                                || c.getStatus().toLowerCase().contains(lower))
                                        .toList();
                }

                // Phân trang thủ công
                int start = (int) pageable.getOffset();
                int end = Math.min(start + pageable.getPageSize(), allCheckins.size());
                List<CheckinResponse> page = (start > allCheckins.size())
                                ? List.of()
                                : allCheckins.subList(start, end).stream()
                                                .map(this::toCheckinResponse)
                                                .toList();

                return new PageImpl<>(page, pageable, allCheckins.size());
        }

        @Override
        @Transactional
        public void deleteCheckin(Long checkinId) {
                StationCheckin checkin = checkinRepository.findById(checkinId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy check-in với ID: " + checkinId));
                checkinRepository.delete(checkin);
        }

        private CheckinResponse toCheckinResponse(StationCheckin checkin) {
                return CheckinResponse.builder()
                                .checkinId(checkin.getCheckinId())
                                .userId(checkin.getUser().getUserId())
                                .fullName(checkin.getUser().getFullName())
                                .avatarUrl(ImageUtil.getAvatarImageUrl(checkin.getUser().getAvatarUrl()))
                                .stationId(checkin.getStation().getStationId())
                                .stationName(checkin.getStation().getName())
                                .status(checkin.getStatus())
                                .imageUrl(checkin.getImageUrl())
                                .createdAt(checkin.getCreatedAt())
                                .build();
        }
}
