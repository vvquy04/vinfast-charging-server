package com.vanquy.evcserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng `station_checkins`.
 * Lưu lịch sử check-in và báo cáo trạng thái trạm sạc từ người dùng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "station_checkins", indexes = {
        @Index(name = "idx_checkin_station", columnList = "station_id"),
        @Index(name = "idx_checkin_user", columnList = "user_id"),
        @Index(name = "idx_checkin_created", columnList = "created_at")
})
public class StationCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkin_id")
    private Long checkinId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    /**
     * Trạng thái trạm sạc mà người dùng báo cáo khi check-in.
     * Giá trị: EMPTY, MODERATE, BUSY, MAINTENANCE
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
