package com.vanquy.evcserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity ánh xạ bảng `charging_stations`.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "charging_stations", indexes = {
        @Index(name = "idx_location", columnList = "latitude, longitude")
})
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long stationId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "opening_hours", length = 100)
    @Builder.Default
    private String openingHours = "24/7";

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "rating", precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConnectorType> connectorTypes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
