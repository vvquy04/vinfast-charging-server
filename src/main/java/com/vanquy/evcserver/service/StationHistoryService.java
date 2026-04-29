package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.response.StationHistoryResponse;

import java.util.List;

public interface StationHistoryService {

    /**
     * Ghi nhận lượt xem trạm sạc (upsert: tạo mới hoặc tăng visit_count).
     */
    void recordVisit(Long userId, Long stationId);

    /**
     * Lấy danh sách trạm đã xem gần đây của user.
     */
    List<StationHistoryResponse> getHistory(Long userId);

    /**
     * Xóa một bản ghi lịch sử.
     */
    void deleteHistory(Long userId, Long historyId);
}
