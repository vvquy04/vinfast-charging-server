package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.response.StationDetailResponse;
import com.vanquy.evcserver.dto.response.StationSummaryResponse;

import java.util.List;

/**
 * Service interface cho Charging Stations.
 */
public interface StationService {

    /**
     * Tìm trạm sạc gần vị trí user (Haversine).
     *
     * @param latitude      vĩ độ user
     * @param longitude     kinh độ user
     * @param radius        bán kính tìm kiếm (km), default 10
     * @param connectorType lọc theo loại cổng sạc (nullable)
     * @param minPowerKw    lọc theo công suất sạc tối thiểu (nullable)
     * @param minRating     lọc theo điểm đánh giá tối thiểu (nullable)
     */
    List<StationSummaryResponse> searchStations(
            double latitude, double longitude,
            double radius, String connectorType,
            Integer minPowerKw, Integer maxPowerKw,
            Double minRating
    );

    /**
     * Lấy chi tiết trạm sạc theo ID (kèm connectorTypes).
     */
    StationDetailResponse getStationDetail(Long stationId);
}
