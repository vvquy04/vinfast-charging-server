package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Tìm kiếm review theo tên user, comment hoặc tên trạm sạc.
     */
    @Query("SELECT r FROM Review r WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(r.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.comment) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.station.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Review> findAllBySearch(@Param("search") String search, Pageable pageable);

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
