package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.request.AdminStationRequest;
import com.vanquy.evcserver.dto.response.*;
import com.vanquy.evcserver.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller quản trị — tất cả API dưới /api/admin/**
 * Yêu cầu JWT token hợp lệ + role = ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ═══════════════════════════════════════════════════
    //  DASHBOARD
    // ═══════════════════════════════════════════════════

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        AdminDashboardStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(Map.of("success", true, "data", stats));
    }

    // ═══════════════════════════════════════════════════
    //  QUẢN LÝ TRẠM SẠC
    // ═══════════════════════════════════════════════════

    @GetMapping("/stations")
    public ResponseEntity<?> getAllStations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Page<StationDetailResponse> data = adminService.getAllStations(
                search, PageRequest.of(page, size, Sort.by("stationId").descending())
        );
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data.getContent(),
                "totalElements", data.getTotalElements(),
                "totalPages", data.getTotalPages(),
                "currentPage", data.getNumber()
        ));
    }

    @PostMapping("/stations")
    public ResponseEntity<?> createStation(@Valid @RequestBody AdminStationRequest request) {
        StationDetailResponse data = adminService.createStation(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tạo trạm sạc thành công",
                "data", data
        ));
    }

    @PutMapping("/stations/{stationId}")
    public ResponseEntity<?> updateStation(
            @PathVariable Long stationId,
            @Valid @RequestBody AdminStationRequest request
    ) {
        StationDetailResponse data = adminService.updateStation(stationId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật trạm sạc thành công",
                "data", data
        ));
    }

    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity<?> deleteStation(@PathVariable Long stationId) {
        adminService.deleteStation(stationId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xóa trạm sạc"
        ));
    }

    // ═══════════════════════════════════════════════════
    //  QUẢN LÝ NGƯỜI DÙNG
    // ═══════════════════════════════════════════════════

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Page<UserProfileResponse> data = adminService.getAllUsers(
                search, PageRequest.of(page, size, Sort.by("userId").descending())
        );
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data.getContent(),
                "totalElements", data.getTotalElements(),
                "totalPages", data.getTotalPages(),
                "currentPage", data.getNumber()
        ));
    }

    @PutMapping("/users/{userId}/toggle-active")
    public ResponseEntity<?> toggleUserActive(@PathVariable Long userId) {
        adminService.toggleUserActive(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã cập nhật trạng thái tài khoản"
        ));
    }

    // ═══════════════════════════════════════════════════
    //  QUẢN LÝ ĐÁNH GIÁ
    // ═══════════════════════════════════════════════════

    @GetMapping("/reviews")
    public ResponseEntity<?> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Page<ReviewResponse> data = adminService.getAllReviews(
                search,
                PageRequest.of(page, size, Sort.by("reviewId").descending())
        );
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data.getContent(),
                "totalElements", data.getTotalElements(),
                "totalPages", data.getTotalPages(),
                "currentPage", data.getNumber()
        ));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        adminService.deleteReview(reviewId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xóa đánh giá"
        ));
    }

    // ═══════════════════════════════════════════════════
    //  QUẢN LÝ CHECK-IN
    // ═══════════════════════════════════════════════════

    @GetMapping("/checkins")
    public ResponseEntity<?> getAllCheckins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Page<com.vanquy.evcserver.dto.response.CheckinResponse> data = adminService.getAllCheckins(
                search, PageRequest.of(page, size, Sort.by("checkinId").descending())
        );
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data.getContent(),
                "totalElements", data.getTotalElements(),
                "totalPages", data.getTotalPages(),
                "currentPage", data.getNumber()
        ));
    }

    @DeleteMapping("/checkins/{checkinId}")
    public ResponseEntity<?> deleteCheckin(@PathVariable Long checkinId) {
        adminService.deleteCheckin(checkinId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xóa check-in"
        ));
    }
}
