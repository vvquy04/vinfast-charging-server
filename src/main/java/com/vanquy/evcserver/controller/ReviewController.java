package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.request.ReviewRequest;
import com.vanquy.evcserver.dto.response.ReviewResponse;
import com.vanquy.evcserver.service.ReviewService;
import com.vanquy.evcserver.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /api/reviews — Tạo đánh giá mới (yêu cầu đăng nhập).
     */
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        ReviewResponse response = reviewService.createReview(userId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đánh giá thành công",
                "data", response
        ));
    }

    /**
     * GET /api/reviews/station/{stationId} — Lấy danh sách review của trạm (public).
     */
    @GetMapping("/station/{stationId}")
    public ResponseEntity<?> getReviewsByStation(
            @PathVariable Long stationId
    ) {
        List<ReviewResponse> response = reviewService.getReviewsByStation(stationId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    /**
     * DELETE /api/reviews/{reviewId} — Xóa đánh giá của mình (yêu cầu đăng nhập).
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        Long userId = SecurityUtil.getCurrentUserId();
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xóa đánh giá"
        ));
    }
}
