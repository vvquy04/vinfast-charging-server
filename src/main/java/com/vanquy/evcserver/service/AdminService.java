package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.request.AdminStationRequest;
import com.vanquy.evcserver.dto.response.AdminDashboardStatsResponse;
import com.vanquy.evcserver.dto.response.ReviewResponse;
import com.vanquy.evcserver.dto.response.StationDetailResponse;
import com.vanquy.evcserver.dto.response.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface cho các chức năng Admin.
 */
public interface AdminService {

    // ─── Dashboard ──────────────────────────────────
    AdminDashboardStatsResponse getDashboardStats();

    // ─── Quản lý Trạm sạc ──────────────────────────
    Page<StationDetailResponse> getAllStations(String search, Pageable pageable);

    StationDetailResponse createStation(AdminStationRequest request);

    StationDetailResponse updateStation(Long stationId, AdminStationRequest request);

    void deleteStation(Long stationId);

    // ─── Quản lý Người dùng ─────────────────────────
    Page<UserProfileResponse> getAllUsers(String search, Pageable pageable);

    void toggleUserActive(Long userId);

    // ─── Quản lý Đánh giá ──────────────────────────
    Page<ReviewResponse> getAllReviews(String search, Pageable pageable);

    void deleteReview(Long reviewId);
}
