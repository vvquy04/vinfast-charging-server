package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.request.ReviewRequest;
import com.vanquy.evcserver.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    /**
     * Tạo đánh giá mới cho trạm sạc.
     */
    ReviewResponse createReview(Long userId, ReviewRequest request);

    /**
     * Lấy danh sách review của trạm sạc.
     */
    List<ReviewResponse> getReviewsByStation(Long stationId);

    /**
     * Xóa đánh giá (chỉ chủ sở hữu).
     */
    void deleteReview(Long userId, Long reviewId);
}
