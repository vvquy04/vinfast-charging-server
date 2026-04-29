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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository stationRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        // 1. Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "userId", userId));

        // 2. Tìm trạm sạc
        ChargingStation station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Trạm sạc", "stationId", request.getStationId()));

        // 3. Tạo review
        Review review = Review.builder()
                .user(user)
                .station(station)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 4. Cập nhật rating trung bình + tổng reviews trên bảng charging_stations
        updateStationRating(station.getStationId());

        return mapToResponse(savedReview);
    }

    @Override
    public PageResponse<ReviewResponse> getReviewsByStation(Long stationId, int page, int size) {
        // Kiểm tra trạm có tồn tại
        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException("Trạm sạc", "stationId", stationId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByStationStationIdOrderByCreatedAtDesc(stationId, pageable);

        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .currentPage(reviewPage.getNumber())
                .build();
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá", "reviewId", reviewId));

        // Kiểm tra quyền sở hữu
        if (!review.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền xóa đánh giá này");
        }

        Long stationId = review.getStation().getStationId();
        reviewRepository.delete(review);

        // Cập nhật lại rating trung bình
        updateStationRating(stationId);
    }

    // ─── Private Helpers ────────────────────────────────────

    /**
     * Cập nhật rating trung bình và total_reviews trên bảng charging_stations.
     */
    private void updateStationRating(Long stationId) {
        Double avgRating = reviewRepository.getAverageRatingByStationId(stationId);
        Integer totalReviews = reviewRepository.countByStationId(stationId);

        ChargingStation station = stationRepository.findById(stationId).orElse(null);
        if (station != null) {
            station.setRating(BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP));
            station.setTotalReviews(totalReviews);
            stationRepository.save(station);
        }
    }

    /**
     * Map entity Review → ReviewResponse DTO.
     */
    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUser().getUserId())
                .fullName(review.getUser().getFullName())
                .avatarUrl(review.getUser().getAvatarUrl())
                .stationId(review.getStation().getStationId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
