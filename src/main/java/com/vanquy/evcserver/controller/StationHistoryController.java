package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.response.StationHistoryResponse;
import com.vanquy.evcserver.service.StationHistoryService;
import com.vanquy.evcserver.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class StationHistoryController {

    private final StationHistoryService historyService;

    /**
     * POST /api/history/{stationId} — Ghi nhận lượt xem trạm sạc.
     */
    @PostMapping("/{stationId}")
    public ResponseEntity<?> recordVisit(@PathVariable Long stationId) {
        Long userId = SecurityUtil.getCurrentUserId();
        historyService.recordVisit(userId, stationId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã ghi nhận lượt xem"
        ));
    }

    /**
     * GET /api/history — Lấy danh sách trạm đã xem gần đây.
     */
    @GetMapping
    public ResponseEntity<?> getMyHistory() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<StationHistoryResponse> history = historyService.getHistory(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", history
        ));
    }

    /**
     * DELETE /api/history/{historyId} — Xóa một bản ghi lịch sử.
     */
    @DeleteMapping("/{historyId}")
    public ResponseEntity<?> deleteHistory(@PathVariable Long historyId) {
        Long userId = SecurityUtil.getCurrentUserId();
        historyService.deleteHistory(userId, historyId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xóa lịch sử"
        ));
    }
}
