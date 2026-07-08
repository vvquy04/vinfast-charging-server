package com.vanquy.evcserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckinResponse {

    private Long checkinId;
    private Long userId;
    private String fullName;
    private String avatarUrl;
    private Long stationId;
    private String stationName;
    private String status;
    private String imageUrl;
    private LocalDateTime createdAt;
}
