package com.vanquy.evcserver.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO chi tiết trạm sạc (GET /api/stations/{id}).
 * Bao gồm connectorTypes + recentReviews.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationDetailResponse {

    private Long stationId;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String openingHours;
    private String imageUrl;
    private BigDecimal rating;
    private Integer totalReviews;

    @JsonProperty("isActive")
    private Boolean isActive;

    private List<ConnectorTypeResponse> connectorTypes;
}
