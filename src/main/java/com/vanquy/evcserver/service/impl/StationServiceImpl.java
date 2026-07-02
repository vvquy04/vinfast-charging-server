package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.dto.response.*;
import com.vanquy.evcserver.exception.ResourceNotFoundException;
import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.ConnectorType;
import com.vanquy.evcserver.repository.ChargingStationRepository;
import com.vanquy.evcserver.repository.ConnectorTypeRepository;
import com.vanquy.evcserver.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.vanquy.evcserver.util.ImageUtil;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final ChargingStationRepository stationRepository;
    private final ConnectorTypeRepository connectorTypeRepository;

    @Override
    public List<StationSummaryResponse> searchStations(
            double latitude, double longitude,
            double radius, String connectorType,
            Integer minPowerKw, Integer maxPowerKw,
            Double minRating
    ) {
        // 1 độ lệch Lat/Lng tương đương với khoảng 111km, 
        // tính toán khung Bounding Box bọc lấy bán kính (giảm tải MySQL)
        double deltaLat = radius / 111.12; 
        double deltaLng = radius / (111.12 * Math.cos(Math.toRadians(latitude)));

        double minLat = latitude - deltaLat;
        double maxLat = latitude + deltaLat;
        double minLng = longitude - deltaLng;
        double maxLng = longitude + deltaLng;

        // Chuẩn hóa connectorType: chuỗi rỗng → null
        String normalizedConnectorType = (connectorType != null && !connectorType.isBlank())
                ? connectorType : null;

        List<Object[]> results = stationRepository.findNearbyStationsFiltered(
                latitude, longitude, radius, normalizedConnectorType,
                minPowerKw, maxPowerKw, minRating,
                minLat, maxLat, minLng, maxLng
        );

        return results.stream()
                .map(this::mapToStationSummary)
                .toList();
    }

    @Override
    public StationDetailResponse getStationDetail(Long stationId) {
        ChargingStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Trạm sạc", "stationId", stationId));

        List<ConnectorTypeResponse> connectors = station.getConnectorTypes().stream()
                .map(this::mapToConnectorResponse)
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

    // ─── Private Helpers ─────────────────────────────

    /**
     * Map native query result (Object[]) → StationSummaryResponse.
     * Thứ tự cột theo SELECT trong native query:
     * station_id, name, address, latitude, longitude,
     * opening_hours, image_url, rating, total_reviews,
     * is_active, created_at, distance
     */
    private StationSummaryResponse mapToStationSummary(Object[] row) {
        Long stationId = ((Number) row[0]).longValue();

        // Lấy connectorTypes cho station này
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
}
