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
    void sendOtp(SendOtpRequest request);

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
     * Đăng nhập bằng Google ID Token.
     * Trả về AuthResponse nếu tài khoản đã tồn tại.
     * Trả về OAuth2RequirePhoneResponse nếu tài khoản chưa tồn tại (chưa có SĐT).
     */
    Object loginWithGoogle(com.vanquy.evcserver.dto.request.OAuth2LoginRequest request);
}
