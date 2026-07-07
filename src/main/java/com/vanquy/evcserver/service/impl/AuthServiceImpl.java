package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.dto.request.LoginRequest;
import com.vanquy.evcserver.dto.request.RegisterRequest;
import com.vanquy.evcserver.dto.request.ResetPasswordRequest;
import com.vanquy.evcserver.dto.request.SendOtpRequest;
import com.vanquy.evcserver.dto.request.VerifyOtpRequest;
import com.vanquy.evcserver.dto.response.AuthResponse;
import com.vanquy.evcserver.exception.BadRequestException;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.service.AuthService;
import com.vanquy.evcserver.util.JwtUtil;
import com.vanquy.evcserver.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vanquy.evcserver.util.ImageUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OtpUtil otpUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String sendOtp(SendOtpRequest request) {
        String phoneNumber = request.getPhoneNumber();
        return otpUtil.generateOtp(phoneNumber);
    }

    @Override
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String otp = request.getOtp();

        // 1. Xác thực OTP
        if (!otpUtil.verifyOtp(phoneNumber, otp)) {
            throw new BadRequestException("Mã OTP không đúng hoặc đã hết hạn");
        }

        // 2. Trả về trạng thái xem SDT này đã có trong hệ thống chưa
        boolean exists = userRepository.existsByPhoneNumber(phoneNumber);

        return AuthResponse.builder()
                .phoneNumber(phoneNumber)
                // Nếu chưa tồn tại -> isNewUser = true -> client chuyển sang màn Đăng ký
                // Nếu đã tồn tại -> isNewUser = false -> client cho phép Đổi mật khẩu hoặc báo lỗi "Tài khoản đã tồn tại" tùy logic của bạn (vd: quên mật khẩu)
                .isNewUser(!exists) 
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String phoneNumber = request.getPhoneNumber();

        // 1. Kiểm tra xem SĐT đã được xác thực OTP chưa
        if (!otpUtil.isPhoneVerified(phoneNumber)) {
            throw new BadRequestException("Vui lòng xác thực số điện thoại bằng OTP trước khi đăng ký");
        }

        // 2. Kiểm tra tài khoản đã tồn tại chưa
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BadRequestException("Số điện thoại này đã được đăng ký");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email này đã được sử dụng");
        }

        // 3. Tạo mới user
        User newUser = User.builder()
                .phoneNumber(phoneNumber)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .gender(com.vanquy.evcserver.util.GenderUtil.standardize(request.getGender()))
                .dateOfBirth(request.getDateOfBirth())
                .vehicleModel(request.getVehicleModel())
                .connectorType(com.vanquy.evcserver.util.ConnectorUtil.standardize(request.getConnectorType()))
                .avatarUrl(ImageUtil.sanitizeAvatarImage(request.getAvatarUrl()))
                .build();

        User savedUser = userRepository.save(newUser);

        // 4. Xóa cờ xác thực OTP
        otpUtil.clearVerifiedPhone(phoneNumber);

        return buildAuthResponse(savedUser, true);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("Sai số điện thoại hoặc mật khẩu"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Sai số điện thoại hoặc mật khẩu");
        }

        if (!user.getIsActive()) {
            throw new BadRequestException("Tài khoản đã bị vô hiệu hóa");
        }

        return buildAuthResponse(user, false);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String phoneNumber = request.getPhoneNumber();

        // 1. Kiểm tra SĐT đã xác thực OTP chưa
        if (!otpUtil.isPhoneVerified(phoneNumber)) {
            throw new BadRequestException("Vui lòng xác thực số điện thoại bằng OTP trước khi đặt lại mật khẩu");
        }

        // 2. Kiểm tra mật khẩu mới và xác nhận khớp nhau
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // 3. Tìm user theo SĐT
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new BadRequestException("Số điện thoại không tồn tại trong hệ thống"));

        // 4. Cập nhật mật khẩu mới
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 5. Xóa cờ xác thực OTP
        otpUtil.clearVerifiedPhone(phoneNumber);
    }

    private AuthResponse buildAuthResponse(User user, boolean isNewUser) {
        String token = jwtUtil.generateToken(user.getUserId());

        return AuthResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(ImageUtil.getAvatarImageUrl(user.getAvatarUrl()))
                .vehicleModel(user.getVehicleModel())
                .connectorType(user.getConnectorType())
                .role(user.getRole())
                .isNewUser(isNewUser)
                .token(token)
                .build();
    }

    @Override
    public boolean checkEmailExists(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }
}

