package com.vanquy.evcserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO cho danh sách trạm sạc (GET /api/stations).
 * Bao gồm distance — khoảng cách từ user đến trạm (km).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationSummaryResponse {

    private Long stationId;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String openingHours;
    private String imageUrl;
    private BigDecimal rating;
    private Integer totalReviews;
    private Double distance;
    private List<ConnectorTypeResponse> connectorTypes;
}
