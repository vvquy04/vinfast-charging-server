package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.request.LoginRequest;
import com.vanquy.evcserver.dto.request.RegisterRequest;
import com.vanquy.evcserver.dto.request.SendOtpRequest;
import com.vanquy.evcserver.dto.request.VerifyOtpRequest;
import com.vanquy.evcserver.dto.response.AuthResponse;

/**
 * Service interface cho Authentication (Phone + Password).
 */
public interface AuthService {

    /**
     * Gửi mã OTP đến số điện thoại.
     */
    String sendOtp(SendOtpRequest request);

    /**
     * Xác thực OTP, đánh dấu SĐT đã được xác thực tạm thời.
     */
    AuthResponse verifyOtp(VerifyOtpRequest request);

    /**
     * Đăng ký tài khoản (Yêu cầu SĐT đã xác thực OTP).
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Đăng nhập bằng SĐT và Password (Không cần OTP).
     */
    AuthResponse login(LoginRequest request);

    /**
     * Kiểm tra xem Email đã tồn tại trong hệ thống chưa.
     */
    boolean checkEmailExists(String email);
}
