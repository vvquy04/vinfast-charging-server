package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.ChargingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {

    List<ChargingSession> findByUserUserIdOrderByStartTimeDesc(Long userId);

    @Query("SELECT c FROM ChargingSession c WHERE c.user.userId = :userId AND c.status = 'CHARGING'")
    Optional<ChargingSession> findActiveSession(@Param("userId") Long userId);
}
