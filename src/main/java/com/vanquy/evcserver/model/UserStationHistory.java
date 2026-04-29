package com.vanquy.evcserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng `user_station_history`.
 * Ghi nhận lịch sử tương tác giữa user và trạm sạc.
 * Mỗi cặp (user, station) chỉ có 1 bản ghi — upsert khi xem lại.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "user_station_history",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_station_hist",
                columnNames = {"user_id", "station_id"}
        )
)
public class UserStationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "visit_count", nullable = false)
    @Builder.Default
    private Integer visitCount = 1;

    @Column(name = "last_visited", nullable = false)
    private LocalDateTime lastVisited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @PrePersist
    protected void onCreate() {
        this.lastVisited = LocalDateTime.now();
    }
}
