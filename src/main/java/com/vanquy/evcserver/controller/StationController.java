package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.response.ApiResponse;
import com.vanquy.evcserver.dto.response.StationDetailResponse;
import com.vanquy.evcserver.dto.response.StationSummaryResponse;
import com.vanquy.evcserver.service.StationService;
import com.vanquy.evcserver.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    /**
     * Tìm trạm sạc gần vị trí với bộ lọc tùy chọn + TOPSIS.
     * GET /api/stations?latitude=...&longitude=...&radius=10
     *     &useTopsis=true&weightDistance=1&weightPower=5&weightOccupancy=1&weightRating=1
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StationSummaryResponse>>> searchStations(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radius,
            @RequestParam(required = false) String connectorType,
            @RequestParam(required = false) Integer minPowerKw,
            @RequestParam(required = false) Integer maxPowerKw,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "false") boolean useTopsis,
            @RequestParam(defaultValue = "1.0") double weightDistance,
            @RequestParam(defaultValue = "1.0") double weightPower,
            @RequestParam(defaultValue = "1.0") double weightOccupancy,
            @RequestParam(defaultValue = "1.0") double weightRating
    ) {
        List<StationSummaryResponse> data = stationService.searchStations(
                latitude, longitude, radius, connectorType,
                minPowerKw, maxPowerKw, minRating,
                useTopsis, weightDistance, weightPower, weightOccupancy, weightRating
        );
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }

    /**
     * Xem chi tiết trạm sạc (kèm Popular Times + Crowd Status).
     * GET /api/stations/{stationId}
     */
    @GetMapping("/{stationId}")
    public ResponseEntity<ApiResponse<StationDetailResponse>> getStationDetail(
            @PathVariable Long stationId
    ) {
        StationDetailResponse data = stationService.getStationDetail(stationId);
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }

    /**
     * Check-in trạm sạc.
     * POST /api/stations/{stationId}/checkin
     * Body: { "status": "EMPTY" | "MODERATE" | "BUSY" | "MAINTENANCE", "imageUrl": "http://..." }
     */
    @PostMapping("/{stationId}/checkin")
    public ResponseEntity<ApiResponse<String>> checkin(
            @PathVariable Long stationId,
            @RequestBody Map<String, String> body
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        String status = body.getOrDefault("status", "EMPTY");
        String imageUrl = body.get("imageUrl");

        stationService.checkin(userId, stationId, status, imageUrl);

        return ResponseEntity.ok(ApiResponse.success(
                "Check-in thành công! Bạn đã nhận được 10 điểm thưởng.", null));
    }
}
