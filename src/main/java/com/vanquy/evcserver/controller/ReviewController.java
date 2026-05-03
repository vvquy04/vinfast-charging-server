package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.request.ReviewRequest;
import com.vanquy.evcserver.dto.response.ApiResponse;
import com.vanquy.evcserver.dto.response.PageResponse;
import com.vanquy.evcserver.dto.response.ReviewResponse;
import com.vanquy.evcserver.service.ReviewService;
import com.vanquy.evcserver.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho Reviews & Ratings.
 * Quản lý đánh giá trạm sạc (xem, tạo, xoá).
 */
@RestController
@RequestMapping("/api/stations/{stationId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * GET /api/stations/{stationId}/reviews?page=0&size=10
     * Lấy danh sách đánh giá của trạm (phân trang).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviews(
            @PathVariable Long stationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReviewResponse> data = reviewService.getReviews(stationId, page, size);
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }

    /**
     * POST /api/stations/{stationId}/reviews
     * Tạo đánh giá mới (yêu cầu đăng nhập).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long stationId,
            @Valid @RequestBody ReviewRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        ReviewResponse data = reviewService.createReview(userId, stationId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đánh giá thành công", data));
    }

    /**
     * DELETE /api/stations/{stationId}/reviews/{reviewId}
     * Xoá đánh giá (chỉ chủ sở hữu).
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Object>> deleteReview(
            @PathVariable Long stationId,
            @PathVariable Long reviewId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa đánh giá"));
    }
}
