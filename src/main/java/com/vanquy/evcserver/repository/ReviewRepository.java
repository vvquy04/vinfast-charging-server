package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Lấy danh sách review của trạm sạc, sắp xếp mới nhất lên trước.
     */
    List<Review> findByStationStationIdOrderByCreatedAtDesc(Long stationId);

    /**
     * Lấy tất cả review của một user.
     */
    List<Review> findByUserUserId(Long userId);

    /**
     * Tính rating trung bình của trạm sạc.
     */
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.station.stationId = :stationId")
    Double getAverageRatingByStationId(@Param("stationId") Long stationId);

    /**
     * Đếm tổng review của trạm sạc.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.station.stationId = :stationId")
    Integer countByStationId(@Param("stationId") Long stationId);
}
