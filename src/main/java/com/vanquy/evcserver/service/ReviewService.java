package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.request.ReviewRequest;
import com.vanquy.evcserver.dto.response.PageResponse;
import com.vanquy.evcserver.dto.response.ReviewResponse;

import java.util.List;

/**
 * Service interface cho Reviews & Ratings.
 */
public interface ReviewService {

    /**
     * Lấy danh sách đánh giá của 1 trạm (phân trang).
     */
    PageResponse<ReviewResponse> getReviews(Long stationId, int page, int size);

    /**
     * Lấy 5 đánh giá mới nhất (dùng cho chi tiết trạm).
     */
    List<ReviewResponse> getRecentReviews(Long stationId);

    /**
     * Tạo đánh giá mới.
     */
    ReviewResponse createReview(Long userId, Long stationId, ReviewRequest request);

    /**
     * Xoá đánh giá (chỉ chủ sở hữu mới được xoá).
     */
    void deleteReview(Long userId, Long reviewId);
}
