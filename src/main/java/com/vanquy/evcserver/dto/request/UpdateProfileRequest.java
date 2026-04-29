package com.vanquy.evcserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO nhận dữ liệu cập nhật hồ sơ người dùng.
 * Tất cả fields đều nullable — chỉ cập nhật field nào được gửi lên.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private String vehicleModel;
    private String connectorType;
}
