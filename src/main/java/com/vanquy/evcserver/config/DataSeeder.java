package com.vanquy.evcserver.config;

import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.ConnectorType;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.ChargingStationRepository;
import com.vanquy.evcserver.repository.ConnectorTypeRepository;
import com.vanquy.evcserver.repository.ReviewRepository;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.repository.UserStationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seed sample data cho charging_stations + connector_types.
 * Cập nhật danh sách 45 trạm sạc tại Hà Nội theo danh sách người dùng cung cấp.
 * Tự động xóa dữ liệu cũ và nạp lại nếu số lượng trạm khác 45.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    
    private final ChargingStationRepository stationRepository;
    private final ConnectorTypeRepository connectorTypeRepository;
    private final ReviewRepository reviewRepository;
    private final UserStationHistoryRepository userStationHistoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // ─── Tạo tài khoản Admin mặc định ────────────────
        seedAdminAccount();

        long currentCount = stationRepository.count();
        if (currentCount == 45) {
            log.info("📦 Database đã có đúng 45 trạm sạc — Bỏ qua seeding để bảo toàn dữ liệu test.");
            return;
        }

        log.info("🧹 Dọn dẹp dữ liệu cũ (reviews, history, connectors, stations) do số lượng hiện tại là {}...", currentCount);
        reviewRepository.deleteAll();
        userStationHistoryRepository.deleteAll();
        connectorTypeRepository.deleteAll();
        stationRepository.deleteAll();

        log.info("🌱 Bắt đầu seed 45 trạm sạc tại Hà Nội...");

        // ─── 1. BA ĐÌNH ──────────────────────────────────
        createStation("Ba Đình - Bãi đỗ xe Khách sạn La Thành",
                "226 Vạn Phúc, Liễu Giai, Ba Đình, Hà Nội",
                21.033500, 105.814200,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Ba Đình - Vincom Center Metropolis",
                "Hầm B3, 29 Liễu Giai, Ba Đình, Hà Nội",
                21.031900, 105.812800,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Ba Đình - Vinhomes Metropolis (Xe máy)",
                "Hầm B1, 29 Liễu Giai, Ba Đình, Hà Nội",
                21.031500, 105.812500,
                "24/7", null,
                new String[][]{{"AC", "11", "10"}});

        createStation("Ba Đình - Vinhomes Metropolis (Ô tô B3)",
                "Hầm B3, 29 Liễu Giai, Ba Đình, Hà Nội",
                21.031000, 105.813000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Ba Đình - Vinhomes Metropolis (Ô tô B4)",
                "Hầm B4, 29 Liễu Giai, Ba Đình, Hà Nội",
                21.030500, 105.812000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 2. CẦU GIẤY ────────────────────────────────
        createStation("Cầu Giấy - Big C Thăng Long",
                "222 Trần Duy Hưng, Trung Hòa, Cầu Giấy, Hà Nội",
                21.006500, 105.797200,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Cầu Giấy - Discovery Complex",
                "Hầm B3, 302 Cầu Giấy, Dịch Vọng, Cầu Giấy, Hà Nội",
                21.034600, 105.794600,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 3. HÀ ĐÔNG ──────────────────────────────────
        createStation("Hà Đông - HPC Landmark 105",
                "Hầm B2, Văn Khê, La Khê, Hà Đông, Hà Nội",
                20.970400, 105.760200,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Hà Đông - Bình Vượng Tower",
                "200 Quang Trung, Quang Trung, Hà Đông, Hà Nội",
                20.967800, 105.772500,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Hà Đông - Vinaconex 21",
                "Ngõ 804 Quang Trung, Phú Lãm, Hà Đông, Hà Nội",
                20.957500, 105.761000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Hà Đông - Dương Nội HH2",
                "Hầm B2, KĐT Dương Nội, Hà Đông, Hà Nội",
                20.969100, 105.751600,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Hà Đông - Rainbow Văn Quán",
                "Đường 19/5, Văn Quán, Hà Đông, Hà Nội",
                20.978100, 105.787700,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 4. TÂY HỒ ──────────────────────────────────
        createStation("Tây Hồ - Somerset West Point",
                "2 Tây Hồ, Quảng An, Tây Hồ, Hà Nội",
                21.062800, 105.828600,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Tây Hồ - Somerset West Lake",
                "254 Thụy Khuê, Thụy Khuê, Tây Hồ, Hà Nội",
                21.043500, 105.819000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Tây Hồ - D’ El Dorado 1",
                "659A Lạc Long Quân, Xuân La, Tây Hồ, Hà Nội",
                21.074300, 105.811800,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Tây Hồ - D’ Le Roi Soleil",
                "59 Xuân Diệu, Quảng An, Tây Hồ, Hà Nội",
                21.064100, 105.826000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 5. LONG BIÊN ───────────────────────────────
        createStation("Long Biên - Vinhomes Symphony",
                "Chu Huy Mân, Phúc Đồng, Long Biên, Hà Nội",
                21.037500, 105.908000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Long Biên - Ruby City 3",
                "Hầm B3, Phúc Lợi, Long Biên, Hà Nội",
                21.042500, 105.928000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Long Biên - Đại lý VinFast Long Biên",
                "1 Nguyễn Văn Linh, Gia Thụy, Long Biên, Hà Nội",
                21.048300, 105.881200,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 6. GIA LÂM ──────────────────────────────────
        createStation("Gia Lâm - Vincom Ocean Park",
                "KĐT Vinhomes Ocean Park, Đa Tốn, Gia Lâm, Hà Nội",
                20.990200, 105.946000,
                "24/7", null,
                new String[][]{{"CCS2", "250", "2"}});

        createStation("Gia Lâm - Trạm sạc S2.01 Ocean Park",
                "Tòa S2.01, Vinhomes Ocean Park, Gia Lâm, Hà Nội",
                20.985600, 105.942200,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Gia Lâm - Trạm sạc S2.03 Ocean Park",
                "Tòa S2.03, Vinhomes Ocean Park, Gia Lâm, Hà Nội",
                20.984500, 105.941500,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Gia Lâm - Trạm sạc TP11 Ocean Park",
                "Tòa TP11, Vinhomes Ocean Park, Gia Lâm, Hà Nội",
                20.988000, 105.944000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Gia Lâm - Trạm sạc Ruby CT01 Ocean Park",
                "Phân khu Ruby, Vinhomes Ocean Park, Gia Lâm, Hà Nội",
                20.986500, 105.948000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 7. HOÀNG MAI ────────────────────────────────
        createStation("Hoàng Mai - Eco Lake View HH1",
                "32 Đại Từ, Đại Kim, Hoàng Mai, Hà Nội",
                20.973400, 105.839500,
                "24/7", null,
                new String[][]{{"AC", "11", "4"}});

        createStation("Hoàng Mai - Eco Lake View HH2",
                "32 Đại Từ, Đại Kim, Hoàng Mai, Hà Nội",
                20.973000, 105.839000,
                "24/7", null,
                new String[][]{{"AC", "11", "4"}});

        createStation("Hoàng Mai - Eco Lake View HH3",
                "32 Đại Từ, Đại Kim, Hoàng Mai, Hà Nội",
                20.972500, 105.838500,
                "24/7", null,
                new String[][]{{"AC", "11", "4"}});

        createStation("Hoàng Mai - Gamuda Gardens",
                "QL1A, Trần Phú, Hoàng Mai, Hà Nội",
                20.970100, 105.867000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 8. ĐỐNG ĐA ──────────────────────────────────
        createStation("Đống Đa - Vincom Nguyễn Chí Thanh",
                "54 Nguyễn Chí Thanh, Láng Thượng, Đống Đa, Hà Nội",
                21.022300, 105.810500,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Đống Đa - Vincom Phạm Ngọc Thạch",
                "2 Phạm Ngọc Thạch, Kim Liên, Đống Đa, Hà Nội",
                21.008900, 105.832700,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 9. HAI BÀ TRƯNG ─────────────────────────────
        createStation("Hai Bà Trưng - Vinhomes Times City",
                "458 Minh Khai, Vĩnh Tuy, Hai Bà Trưng, Hà Nội",
                20.995700, 105.868600,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Hai Bà Trưng - Vinmec Times City",
                "458 Minh Khai, Vĩnh Tuy, Hai Bà Trưng, Hà Nội",
                20.996500, 105.869000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 10. THANH OAI ──────────────────────────────
        createStation("Thanh Oai - KĐT Thanh Hà HH01",
                "KĐT Thanh Hà, Cự Khê, Thanh Oai, Hà Nội",
                20.932000, 105.795000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "2"}, {"AC", "22", "2"}});

        createStation("Thanh Oai - KĐT Thanh Hà HH03",
                "KĐT Thanh Hà, Cự Khê, Thanh Oai, Hà Nội",
                20.931000, 105.796000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "2"}, {"AC", "22", "2"}});

        createStation("Thanh Oai - Bãi đỗ xe Thanh Hà",
                "KĐT Thanh Hà, Cự Khê, Thanh Oai, Hà Nội",
                20.929000, 105.793000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 11. HOÀI ĐỨC ───────────────────────────────
        createStation("Hoài Đức - Chung cư Splendora",
                "Hầm B1, Splendora Bắc An Khánh, Hoài Đức, Hà Nội",
                21.006200, 105.725000,
                "24/7", null,
                new String[][]{{"AC", "11", "4"}});

        createStation("Hoài Đức - Nam An Khánh",
                "KĐT Nam An Khánh, An Khánh, Hoài Đức, Hà Nội",
                20.999000, 105.718000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 12. BA VÌ ──────────────────────────────────
        createStation("Ba Vì - Trạm dừng nghỉ Sữa Ất Thảo 2",
                "Láng Hòa Lạc, Yên Bài, Ba Vì, Hà Nội",
                21.012500, 105.479500,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Ba Vì - Sữa Chị Vàng",
                "QL32, Tản Lĩnh, Ba Vì, Hà Nội",
                21.096500, 105.385000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Ba Vì - Cây xăng Phú Sơn",
                "QL32, Phú Sơn, Ba Vì, Hà Nội",
                21.144000, 105.378000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 13. BẮC TỪ LIÊM ────────────────────────────
        createStation("Bắc Từ Liêm - Chung cư C2 Xuân Đỉnh",
                "Đường Đỗ Nhuận, Xuân Đỉnh, Bắc Từ Liêm, Hà Nội",
                21.074000, 105.798000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Bắc Từ Liêm - Vincom Bắc Từ Liêm",
                "234 Phạm Văn Đồng, Cổ Nhuế, Bắc Từ Liêm, Hà Nội",
                21.050500, 105.782000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Bắc Từ Liêm - Showroom VinFast Phạm Văn Đồng",
                "166 Phạm Văn Đồng, Xuân Đỉnh, Bắc Từ Liêm, Hà Nội",
                21.045000, 105.783500,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        // ─── 14. SÓC SƠN ────────────────────────────────
        createStation("Sóc Sơn - Bãi đỗ xe Gia Linh",
                "TT Sóc Sơn, Sóc Sơn, Hà Nội",
                21.258000, 105.850000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        createStation("Sóc Sơn - Cây xăng Total Phú Minh",
                "Sân bay Nội Bài, Phú Minh, Sóc Sơn, Hà Nội",
                21.218500, 105.811000,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}});

        log.info("✅ Đã seed xong {} trạm sạc tại Hà Nội.", stationRepository.count());
    }

    private void createStation(
            String name, String address,
            double lat, double lng,
            String openingHours, String imageUrl,
            String[][] connectors
    ) {
        ChargingStation station = ChargingStation.builder()
                .name(name)
                .address(address)
                .latitude(BigDecimal.valueOf(lat))
                .longitude(BigDecimal.valueOf(lng))
                .openingHours(openingHours)
                .imageUrl(imageUrl)
                .build();

        for (String[] conn : connectors) {
            ConnectorType ct = ConnectorType.builder()
                    .type(conn[0])
                    .powerKw(Integer.parseInt(conn[1]))
                    .totalPorts(Integer.parseInt(conn[2]))
                    .station(station)
                    .build();
            station.getConnectorTypes().add(ct);
        }

        stationRepository.save(station);
    }

    /**
     * Tạo tài khoản Admin mặc định nếu chưa tồn tại.
     * SĐT: 0999999999 / Mật khẩu: admin123
     */
    private void seedAdminAccount() {
        String adminPhone = "0999999999";
        if (userRepository.existsByPhoneNumber(adminPhone)) {
            log.info("👤 Tài khoản Admin đã tồn tại — Bỏ qua.");
            return;
        }

        User admin = User.builder()
                .fullName("Quản trị viên")
                .phoneNumber(adminPhone)
                .email("admin@evcpoint.vn")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role("ADMIN")
                .isActive(true)
                .build();
        userRepository.save(admin);
        log.info("✅ Đã tạo tài khoản Admin mặc định — SĐT: {} / Mật khẩu: admin123", adminPhone);
    }
}
