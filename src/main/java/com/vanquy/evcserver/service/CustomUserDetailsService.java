package com.vanquy.evcserver.service;

import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService — load user từ database theo userId.
 * Spring Security dùng class này trong JWT filter để xác thực user.
 *
 * Lưu ý: username ở đây thực chất là userId (String).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by userId (truyền vào dưới dạng String).
     */
    @Override
    public UserDetails loadUserByUsername(String userIdStr) throws UsernameNotFoundException {
        Long userId = Long.parseLong(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User không tìm thấy với ID: " + userId
                ));

        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getUserId()))
                .password("{noop}OTP_AUTHENTICATED")
                .roles(user.getRole()) // Đọc role từ database (USER hoặc ADMIN)
                .build();
    }
}
