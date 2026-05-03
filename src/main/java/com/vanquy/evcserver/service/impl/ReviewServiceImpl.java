package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.dto.request.ReviewRequest;
import com.vanquy.evcserver.dto.response.PageResponse;
import com.vanquy.evcserver.dto.response.ReviewResponse;
import com.vanquy.evcserver.exception.BadRequestException;
import com.vanquy.evcserver.exception.ResourceNotFoundException;
import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.Review;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.ChargingStationRepository;
import com.vanquy.evcserver.repository.ReviewRepository;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Triển khai ReviewService.
 * Xử lý CRUD đánh giá + tự động cập nhật rating trung bình của trạm.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ChargingStationRepository stationRepository;
    private final UserRepository userRepository;

    @Override
    public PageResponse<ReviewResponse> getReviews(Long stationId, int page, int size) {
        // Kiểm tra trạm tồn tại
        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException("Không tìm thấy trạm sạc với ID: " + stationId);
        }

        Page<Review> reviewPage = reviewRepository.findByStationStationIdOrderByCreatedAtDesc(
                stationId, PageRequest.of(page, size)
        );

        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .currentPage(reviewPage.getNumber())
                .build();
    }

    @Override
    public List<ReviewResponse> getRecentReviews(Long stationId) {
        return reviewRepository.findTop5ByStationStationIdOrderByCreatedAtDesc(stationId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, Long stationId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        ChargingStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạm sạc với ID: " + stationId));

        Review review = Review.builder()
                .user(user)
                .station(station)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        // Tự động cập nhật rating trung bình và tổng review của trạm
        recalculateStationRating(station);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá với ID: " + reviewId));

        // Kiểm tra quyền sở hữu
        if (!review.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền xoá đánh giá này");
        }

        ChargingStation station = review.getStation();
        reviewRepository.delete(review);

        // Cập nhật lại rating trung bình
        recalculateStationRating(station);
    }

    /**
     * Tính lại AVG(rating) và COUNT(*) cho trạm, rồi cập nhật vào entity.
     */
    private void recalculateStationRating(ChargingStation station) {
        Double avg = reviewRepository.calculateAverageRating(station.getStationId());
        long count = reviewRepository.countByStationStationId(station.getStationId());

        station.setRating(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
        station.setTotalReviews((int) count);
        stationRepository.save(station);
    }

    /**
     * Chuyển Review entity → ReviewResponse DTO.
     */
    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUser().getUserId())
                .userName(review.getUser().getFullName())
                .userAvatarUrl(review.getUser().getAvatarUrl())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
