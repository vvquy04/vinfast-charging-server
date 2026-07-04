package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.dto.request.ChangePasswordRequest;
import com.vanquy.evcserver.dto.request.UpdateProfileRequest;
import com.vanquy.evcserver.dto.response.UserProfileResponse;
import com.vanquy.evcserver.exception.BadRequestException;
import com.vanquy.evcserver.exception.ResourceNotFoundException;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vanquy.evcserver.util.ImageUtil;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "userId", userId));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "userId", userId));

        // Partial update — chỉ ghi đè field nào client gửi lên (khác null)
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            // Kiểm tra email trùng
            if (!request.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email này đã được sử dụng");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(ImageUtil.sanitizeAvatarImage(request.getAvatarUrl()));
        }
        if (request.getVehicleModel() != null) {
            user.setVehicleModel(request.getVehicleModel());
        }
        if (request.getConnectorType() != null) {
            user.setConnectorType(request.getConnectorType());
        }

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    // ─── Private Helper ─────────────────────────────────────

    private UserProfileResponse mapToResponse(User user) {
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(ImageUtil.getAvatarImageUrl(user.getAvatarUrl()))
                .vehicleModel(user.getVehicleModel())
                .connectorType(user.getConnectorType())
                .createdAt(user.getCreatedAt())
                .rewardPoints(user.getRewardPoints() != null ? user.getRewardPoints() : 0)
                .build();
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "userId", userId));

        // 1. Xác thực mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng");
        }

        // 2. Kiểm tra mật khẩu mới và xác nhận khớp nhau
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // 3. Kiểm tra mật khẩu mới không trùng mật khẩu cũ
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BadRequestException("Mật khẩu mới phải khác mật khẩu hiện tại");
        }

        // 4. Cập nhật mật khẩu mới
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
