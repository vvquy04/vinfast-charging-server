package com.vanquy.evcserver.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private String vehicleModel;
    private String connectorType;
    private String role;

    @JsonProperty("isActive")
    private Boolean isActive;

    private LocalDateTime createdAt;
    private Integer rewardPoints;
}
