package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.response.ApiResponse;
import com.vanquy.evcserver.dto.response.PageResponse;
import com.vanquy.evcserver.dto.response.StationDetailResponse;
import com.vanquy.evcserver.dto.response.StationSummaryResponse;
import com.vanquy.evcserver.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    /**
     * Tìm trạm sạc gần vị trí (Haversine).
     * GET /api/stations?latitude=10.84&longitude=106.84&radius=10&connectorType=CCS2&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StationSummaryResponse>>> searchStations(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radius,
            @RequestParam(required = false) String connectorType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<StationSummaryResponse> data = stationService.searchStations(
                latitude, longitude, radius, connectorType, page, size
        );
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }

    /**
     * Xem chi tiết trạm sạc.
     * GET /api/stations/{stationId}
     */
    @GetMapping("/{stationId}")
    public ResponseEntity<ApiResponse<StationDetailResponse>> getStationDetail(
            @PathVariable Long stationId
    ) {
        StationDetailResponse data = stationService.getStationDetail(stationId);
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }
}
