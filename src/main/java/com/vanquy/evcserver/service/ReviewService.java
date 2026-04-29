package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.request.ReviewRequest;
import com.vanquy.evcserver.dto.response.PageResponse;
import com.vanquy.evcserver.dto.response.ReviewResponse;

public interface ReviewService {

    /**
     * Tạo đánh giá mới cho trạm sạc.
     */
    ReviewResponse createReview(Long userId, ReviewRequest request);

    /**
     * Lấy danh sách review của trạm sạc (phân trang).
     */
    PageResponse<ReviewResponse> getReviewsByStation(Long stationId, int page, int size);

    /**
     * Xóa đánh giá (chỉ chủ sở hữu).
     */
    void deleteReview(Long userId, Long reviewId);
}
