package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.config.VnPayConfig;
import com.vanquy.evcserver.dto.request.StartChargingRequest;
import com.vanquy.evcserver.dto.request.StopChargingRequest;
import com.vanquy.evcserver.dto.request.VnPayPaymentRequest;
import com.vanquy.evcserver.model.ChargingSession;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.service.ChargingSessionService;
import com.vanquy.evcserver.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChargingController {

    private final ChargingSessionService chargingSessionService;
    private final UserRepository userRepository;

    @PostMapping("/charging/start")
    public ResponseEntity<?> startCharging(@RequestBody StartChargingRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        ChargingSession session = chargingSessionService.startSession(
                userId, request.getStationId(), request.getConnectorType(), request.getPowerKw()
        );
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Kích hoạt trụ sạc thành công!",
                "data", session
        ));
    }

    @PostMapping("/charging/stop")
    public ResponseEntity<?> stopCharging(@RequestBody StopChargingRequest request) {
        ChargingSession session = chargingSessionService.stopSession(
                request.getSessionId(), request.getEnergyCharged()
        );
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Phiên sạc đã dừng, hóa đơn đã được thanh toán!",
                "data", session
        ));
    }

    @GetMapping("/charging/active")
    public ResponseEntity<?> getActiveSession() {
        Long userId = SecurityUtil.getCurrentUserId();
        ChargingSession session = chargingSessionService.getActiveSession(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", session != null ? session : Map.of()
        ));
    }

    @GetMapping("/charging/history")
    public ResponseEntity<?> getChargingHistory() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ChargingSession> sessions = chargingSessionService.getSessionHistory(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", sessions
        ));
    }

    // ═══════════════════════════════════════════════════
    // VÍ ĐIỆN TỬ & VNPAY PAYMENT GATEWAY
    // ═══════════════════════════════════════════════════

    @PostMapping("/payment/vnpay/create-url")
    public ResponseEntity<?> createVnPayUrl(@RequestBody VnPayPaymentRequest request, HttpServletRequest servletRequest) {
        Long userId = SecurityUtil.getCurrentUserId();
        long amount = request.getAmount() * 100L; // VNPay sử dụng xu (1đ x 100)

        String vnp_TxnRef = userId + "_" + VnPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VnPayConfig.getIpAddress(servletRequest);

        // Sinh link callback động dựa trên host/port thực tế của server đang chạy
        String scheme = servletRequest.getScheme();
        String serverName = servletRequest.getServerName();
        int serverPort = servletRequest.getServerPort();
        String baseUrl = scheme + "://" + serverName + ":" + serverPort;
        String vnp_ReturnUrl = baseUrl + "/api/payment/vnpay/callback";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VnPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Nap tien vi dien tu EVCPoint: " + request.getAmount() + " VND");
        vnp_Params.put("vnp_OrderType", VnPayConfig.vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        List<String> hashParts = new ArrayList<>();
        List<String> queryParts = new ArrayList<>();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    String encodedKey = URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()).replace("+", "%20");
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()).replace("+", "%20");

                    hashParts.add(fieldName + "=" + fieldValue);
                    queryParts.add(encodedKey + "=" + encodedValue);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        String hashDataString = String.join("&", hashParts);
        String queryUrl = String.join("&", queryParts);

        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashDataString);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;

        return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentUrl", paymentUrl
        ));
    }

    @GetMapping("/payment/vnpay/callback")
    public ResponseEntity<String> vnpayCallback(
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_Amount") Long amountCents,
            @RequestParam("vnp_TxnRef") String txnRef
    ) {
        Long userId = Long.parseLong(txnRef.split("_")[0]);
        boolean success = "00".equals(responseCode);
        if (success) {
            double depositAmount = amountCents / 100.0;
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                double current = user.getBalance() != null ? user.getBalance() : 0.0;
                user.setBalance(current + depositAmount);
                userRepository.save(user);
            }
        }

        String htmlContent = "<html><head><meta charset=\"UTF-8\"><title>EVCPoint Payment</title>" +
                "<style>body { font-family: sans-serif; text-align: center; padding-top: 50px; background-color: #f7f9fc; color: #333; }" +
                ".container { max-width: 400px; margin: 0 auto; padding: 30px; background: white; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                "h1 { color: " + (success ? "#43A047" : "#E53935") + "; }" +
                ".btn { display: inline-block; margin-top: 20px; padding: 10px 20px; background: #000; color: #fff; text-decoration: none; border-radius: 6px; font-weight: bold; }</style></head>" +
                "<body><div class=\"container\">" +
                "<h1>" + (success ? "Thanh toán thành công!" : "Thanh toán thất bại!") + "</h1>" +
                "<p>" + (success ? "Số dư ví điện tử EVCPoint của bạn đã được cập nhật thành công." : "Giao dịch thanh toán nạp tiền bị hủy hoặc thất bại.") + "</p>" +
                "<p>Bạn có thể đóng trang này để quay lại ứng dụng.</p>" +
                "<a class=\"btn\" href=\"evcpoint://payment/success?status=" + (success ? "success" : "failed") + "\">Quay lại Ứng dụng</a>" +
                "</div></body></html>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
    }

    @PostMapping("/payment/deposit")
    public ResponseEntity<?> mockDeposit(@RequestBody VnPayPaymentRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        double current = user.getBalance() != null ? user.getBalance() : 0.0;
        user.setBalance(current + request.getAmount());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Nạp ví ảo thành công!",
                "data", user.getBalance()
        ));
    }
}
