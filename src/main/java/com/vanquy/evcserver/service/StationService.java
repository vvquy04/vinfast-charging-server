package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.response.StationDetailResponse;
import com.vanquy.evcserver.dto.response.StationSummaryResponse;

import java.util.List;

/**
 * Service interface cho Charging Stations.
 */
public interface StationService {

    /**
     * Tìm trạm sạc gần vị trí user, hỗ trợ TOPSIS.
     *
     * @param latitude        vĩ độ user
     * @param longitude       kinh độ user
     * @param radius          bán kính tìm kiếm (km)
     * @param connectorType   lọc theo loại cổng sạc (nullable)
     * @param minPowerKw      lọc theo công suất sạc tối thiểu (nullable)
     * @param maxPowerKw      lọc theo công suất sạc tối đa (nullable)
     * @param minRating       lọc theo điểm đánh giá tối thiểu (nullable)
     * @param useTopsis       có áp dụng thuật toán TOPSIS hay không
     * @param weightDistance   trọng số tiêu chí khoảng cách
     * @param weightPower      trọng số tiêu chí công suất
     * @param weightOccupancy  trọng số tiêu chí đông đúc
     * @param weightRating     trọng số tiêu chí đánh giá
     */
    List<StationSummaryResponse> searchStations(
            double latitude, double longitude,
            double radius, String connectorType,
            Integer minPowerKw, Integer maxPowerKw,
            Double minRating,
            boolean useTopsis,
            double weightDistance, double weightPower,
            double weightOccupancy, double weightRating
    );

    /**
     * Lấy chi tiết trạm sạc theo ID (kèm connectorTypes, popularTimes, crowdStatus).
     */
    StationDetailResponse getStationDetail(Long stationId);

    /**
     * Người dùng check-in tại trạm sạc và báo cáo trạng thái.
     *
     * @param userId     ID người dùng đã xác thực
     * @param stationId  ID trạm sạc
     * @param status     Trạng thái báo cáo (EMPTY, MODERATE, BUSY, MAINTENANCE)
     * @param imageUrl   Đường dẫn ảnh chụp check-in thực tế (nullable)
     */
    void checkin(Long userId, Long stationId, String status, String imageUrl);
}
