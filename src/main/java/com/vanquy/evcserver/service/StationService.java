package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.response.PageResponse;
import com.vanquy.evcserver.dto.response.StationDetailResponse;
import com.vanquy.evcserver.dto.response.StationSummaryResponse;

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
     * @param page          trang hiện tại (0-indexed)
     * @param size          số kết quả mỗi trang
     */
    PageResponse<StationSummaryResponse> searchStations(
            double latitude, double longitude,
            double radius, String connectorType,
            int page, int size
    );

    /**
     * Lấy chi tiết trạm sạc theo ID (kèm connectorTypes).
     */
    StationDetailResponse getStationDetail(Long stationId);
}
