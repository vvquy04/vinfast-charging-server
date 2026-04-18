package com.vanquy.evcserver.config;

import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.ConnectorType;
import com.vanquy.evcserver.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seed sample data cho charging_stations + connector_types.
 * Chỉ insert khi database chưa có data (count == 0).
 *
 * Dữ liệu mẫu: 10 trạm sạc VinFast thực tế tại TP.HCM và Hà Nội.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final ChargingStationRepository stationRepository;

    @Override
    public void run(String... args) {
        if (stationRepository.count() > 0) {
            log.info("📦 Database đã có data — bỏ qua seeding.");
            return;
        }

        log.info("🌱 Seeding sample charging stations...");

        // ─── TP.HCM ──────────────────────────────────
        createStation("VinFast Charging - Vinhomes Grand Park",
                "Đường Nguyễn Xiển, TP. Thủ Đức, TP.HCM",
                10.840235, 106.843820,
                "24/7", null,
                new String[][]{{"CCS2", "60", "4"}, {"AC", "22", "2"}});

        createStation("VinFast Charging - Vinhomes Central Park",
                "720A Điện Biên Phủ, Bình Thạnh, TP.HCM",
                10.794700, 106.721900,
                "24/7", null,
                new String[][]{{"CCS2", "150", "6"}, {"AC", "11", "4"}, {"Type2", "22", "2"}});

        createStation("VinFast Charging - Vincom Đồng Khởi",
                "72 Lê Thánh Tôn, Quận 1, TP.HCM",
                10.777000, 106.703000,
                "06:00 - 23:00", null,
                new String[][]{{"CCS2", "60", "2"}, {"CHAdeMO", "50", "2"}});

        createStation("VinFast Charging - AEON Tân Phú",
                "30 Bờ Bao Tân Thắng, Tân Phú, TP.HCM",
                10.801500, 106.618700,
                "08:00 - 22:00", null,
                new String[][]{{"CCS2", "60", "4"}, {"AC", "22", "2"}});

        createStation("VinFast Charging - Landmark 81",
                "208 Nguyễn Hữu Cảnh, Bình Thạnh, TP.HCM",
                10.795100, 106.722100,
                "24/7", null,
                new String[][]{{"CCS2", "150", "8"}, {"DC", "60", "4"}, {"AC", "11", "4"}});

        // ─── Hà Nội ──────────────────────────────────
        createStation("VinFast Charging - Vinhomes Ocean Park",
                "Đa Tốn, Gia Lâm, Hà Nội",
                20.980500, 105.945200,
                "24/7", null,
                new String[][]{{"CCS2", "150", "6"}, {"AC", "22", "4"}});

        createStation("VinFast Charging - Vincom Bà Triệu",
                "191 Bà Triệu, Hai Bà Trưng, Hà Nội",
                21.011500, 105.849000,
                "06:00 - 23:00", null,
                new String[][]{{"CCS2", "60", "2"}, {"Type2", "22", "2"}});

        createStation("VinFast Charging - Times City",
                "458 Minh Khai, Hai Bà Trưng, Hà Nội",
                20.995700, 105.868600,
                "24/7", null,
                new String[][]{{"CCS2", "150", "4"}, {"AC", "11", "4"}, {"CHAdeMO", "50", "2"}});

        createStation("VinFast Charging - Royal City",
                "72A Nguyễn Trãi, Thanh Xuân, Hà Nội",
                20.994800, 105.815600,
                "06:00 - 23:00", null,
                new String[][]{{"CCS2", "60", "4"}, {"AC", "22", "2"}});

        createStation("VinFast Charging - Vinhomes Smart City",
                "Đại lộ Thăng Long, Nam Từ Liêm, Hà Nội",
                21.007200, 105.756800,
                "24/7", null,
                new String[][]{{"CCS2", "150", "6"}, {"DC", "60", "4"}, {"AC", "22", "4"}, {"Type2", "22", "2"}});

        log.info("✅ Seeded {} charging stations.", stationRepository.count());
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
}
