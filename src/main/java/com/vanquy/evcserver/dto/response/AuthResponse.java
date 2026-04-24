package com.vanquy.evcserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về sau khi xác thực thành công (Google hoặc OTP).
 * isNewUser giúp mobile app biết cần chuyển đến trang đăng ký hay Home.
 */
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private String vehicleModel;
    private String connectorType;
    private boolean isNewUser;
    private String token;
}
