package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserUserIdOrderByBookingTimeDesc(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.user.userId = :userId AND b.status = 'PENDING' AND b.expiryTime > :now")
    Optional<Booking> findActiveBooking(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.expiryTime <= :now")
    List<Booking> findExpiredBookings(@Param("now") LocalDateTime now);
}
