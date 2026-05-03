package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho bảng `reviews`.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Lấy danh sách đánh giá của 1 trạm (phân trang).
     */
    Page<Review> findByStationStationIdOrderByCreatedAtDesc(Long stationId, Pageable pageable);

    /**
     * Lấy 5 đánh giá mới nhất của 1 trạm (dùng trong chi tiết trạm).
     */
    List<Review> findTop5ByStationStationIdOrderByCreatedAtDesc(Long stationId);

    /**
     * Tính rating trung bình của 1 trạm.
     */
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.station.stationId = :stationId")
    Double calculateAverageRating(Long stationId);

    /**
     * Đếm tổng số đánh giá của 1 trạm.
     */
    long countByStationStationId(Long stationId);
}
