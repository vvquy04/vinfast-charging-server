package com.vanquy.evcserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request body cho cập nhật thông tin cá nhân.
 * Chỉ gửi các field muốn thay đổi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    private String fullName;
    private String phoneNumber;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private String vehicleModel;
    private String connectorType;
}
