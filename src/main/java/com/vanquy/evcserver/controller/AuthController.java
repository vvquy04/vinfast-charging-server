package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.request.LoginRequest;
import com.vanquy.evcserver.dto.request.RegisterRequest;
import com.vanquy.evcserver.dto.request.SendOtpRequest;
import com.vanquy.evcserver.dto.request.VerifyOtpRequest;
import com.vanquy.evcserver.dto.response.ApiResponse;
import com.vanquy.evcserver.dto.response.AuthResponse;
import com.vanquy.evcserver.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Object>> sendOtp(
            @Valid @RequestBody SendOtpRequest request
    ) {
        String otp = authService.sendOtp(request);

        String maskedPhone = request.getPhoneNumber().substring(0, 4)
                + "****"
                + request.getPhoneNumber().substring(8);

        return ResponseEntity.ok(
                ApiResponse.success("Mã OTP đã được gửi đến " + maskedPhone, java.util.Map.of("otp", otp))
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        AuthResponse data = authService.verifyOtp(request);
        String message = data.isNewUser() ? "Xác thực OTP thành công. Vui lòng tạo mật khẩu." : "Xác thực OTP thành công.";
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse data = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", data));
    }
}
