package com.vanquy.evcserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class quản lý OTP (One-Time Password).
 * Lưu OTP trong memory (ConcurrentHashMap) — phù hợp cho development/demo.
 *
 * Lưu ý: Production nên dùng Redis hoặc database.
 */
@Component
public class OtpUtil {

    private static final Logger log = LoggerFactory.getLogger(OtpUtil.class);
    private static final int OTP_LENGTH = 4;
    private static final int OTP_EXPIRY_MINUTES = 5;

    private final SecureRandom random = new SecureRandom();

    /**
     * Lưu OTP theo phoneNumber → {otp, expiryTime}
     */
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    /**
     * Lưu trạng thái SĐT đã verify OTP thành công (hết hạn sau 15p)
     */
    private final Map<String, LocalDateTime> verifiedPhones = new ConcurrentHashMap<>();

    /**
     * Tạo OTP 4 chữ số và lưu vào memory.
     *
     * @param phoneNumber số điện thoại
     * @return mã OTP 4 số
     */
    public String generateOtp(String phoneNumber) {
        // Tạo OTP random 4 chữ số (1000-9999)
        int otp = 1000 + random.nextInt(9000);
        String otpStr = String.valueOf(otp);

        // Lưu vào store với thời gian hết hạn
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStore.put(phoneNumber, new OtpData(otpStr, expiryTime));

        // Log ra console (development mode — thay vì gửi SMS thật)
        log.info("========================================");
        log.info("OTP for number {}: {}", phoneNumber, otpStr);
        log.info("Expires at: {}", expiryTime);
        log.info("========================================");

        return otpStr;
    }

    /**
     * Xác thực OTP.
     *
     * @param phoneNumber số điện thoại
     * @param otp         mã OTP cần verify
     * @return true nếu OTP đúng và chưa hết hạn
     */
    public boolean verifyOtp(String phoneNumber, String otp) {
        OtpData otpData = otpStore.get(phoneNumber);

        if (otpData == null) {
            return false;
        }

        // Kiểm tra hết hạn
        if (LocalDateTime.now().isAfter(otpData.expiryTime())) {
            otpStore.remove(phoneNumber);
            return false;
        }

        // Kiểm tra OTP khớp
        if (otpData.otp().equals(otp)) {
            otpStore.remove(phoneNumber); // Xóa sau khi dùng (one-time)
            verifiedPhones.put(phoneNumber, LocalDateTime.now().plusMinutes(15)); // Cho phép đăng ký trong 15p
            return true;
        }

        return false;
    }

    /**
     * Kiểm tra SĐT đã được xác thực qua OTP chưa.
     */
    public boolean isPhoneVerified(String phoneNumber) {
        LocalDateTime expiry = verifiedPhones.get(phoneNumber);
        if (expiry == null) return false;
        if (LocalDateTime.now().isAfter(expiry)) {
            verifiedPhones.remove(phoneNumber);
            return false;
        }
        return true;
    }

    /**
     * Xóa trạng thái đã xác thực sau khi đăng ký thành công.
     */
    public void clearVerifiedPhone(String phoneNumber) {
        verifiedPhones.remove(phoneNumber);
    }

    /**
     * Kiểm tra xem số điện thoại có đang chờ OTP không (tránh spam gửi liên tục).
     */
    public boolean hasActiveOtp(String phoneNumber) {
        OtpData otpData = otpStore.get(phoneNumber);
        if (otpData == null) return false;
        if (LocalDateTime.now().isAfter(otpData.expiryTime())) {
            otpStore.remove(phoneNumber);
            return false;
        }
        return true;
    }

    /**
     * Record lưu trữ OTP + thời gian hết hạn.
     */
    private record OtpData(String otp, LocalDateTime expiryTime) {}
}
