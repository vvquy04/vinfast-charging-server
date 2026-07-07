package com.vanquy.evcserver.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp tiện ích sinh dữ liệu biểu đồ Popular Times
 * cho trạm sạc dựa trên stationId, dayOfWeek và dữ liệu check-in thực tế của người dùng.
 */
public class PopularTimesUtil {

    private static final String[] DAY_NAMES = {
            "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"
    };

    /**
     * Tạo dữ liệu Popular Times cho toàn bộ 7 ngày trong tuần.
     * Sử dụng thuật toán lai trộn (hybrid blending) giữa biểu đồ nền và dữ liệu thực tế.
     *
     * @param stationId     ID trạm sạc
     * @param checkinCounts Kết quả truy vấn đếm check-in từ Repository (Object[] gồm: day_of_week, hour_of_day, count)
     * @return Map gồm key là thứ tiếng Việt và value là danh sách 24 số nguyên (0-100)
     */
    public static Map<String, List<Integer>> getWeeklyPopularTimes(Long stationId, List<Object[]> checkinCounts) {
        //Phân tích kết quả đếm check-in từ database thành mảng 2 chiều 7 ngày x 24 giờ
        int[][] actualCounts = new int[7][24];
        int totalCheckins = 0;
        int maxHourlyCheckin = 0;

        if (checkinCounts != null) {
            for (Object[] row : checkinCounts) {
                int dayOfWeek = ((Number) row[0]).intValue(); // 1 = Sunday, 2 = Monday, ...
                int hour = ((Number) row[1]).intValue();       // 0..23
                int count = ((Number) row[2]).intValue();

                // Ánh xạ: 2 (Monday) -> 0, ..., 7 (Saturday) -> 5, 1 (Sunday) -> 6
                int dayIndex = (dayOfWeek == 1) ? 6 : (dayOfWeek - 2);
                if (dayIndex >= 0 && dayIndex < 7 && hour >= 0 && hour < 24) {
                    actualCounts[dayIndex][hour] = count;
                    totalCheckins += count;
                    if (count > maxHourlyCheckin) {
                        maxHourlyCheckin = count;
                    }
                }
            }
        }

        //Tính trọng số alpha dựa trên tổng số lượt check-in (càng nhiều check-in, độ tin cậy càng cao)
        // Nếu trạm sạc có từ 50 lượt check-in trở lên, sử dụng 100% dữ liệu thực tế
        double alpha = Math.min(1.0, (double) totalCheckins / 50.0);

        Map<String, List<Integer>> weeklyData = new LinkedHashMap<>();

        for (int d = 0; d < 7; d++) {
            List<Integer> hoursData = new ArrayList<>(24);
            int dayOfWeekNumber = d + 1; // 1 = Monday, ..., 7 = Sunday (cho hàm getBaseValue)

            for (int hour = 0; hour < 24; hour++) {
                //Tính giá trị nền của trạm sạc
                double base = getBaseValue(hour, dayOfWeekNumber);
                double seed = Math.sin(stationId * 7.0 + dayOfWeekNumber * 13.0 + hour * 37.0);
                int variation = (int) (seed * 15.0);
                int baseVal = (int) Math.max(0, Math.min(100, base + variation));

                //Tính giá trị thực tế chuẩn hóa (0-100) dựa trên giờ cao điểm nhất của trạm đó
                double actualRate = 0.0;
                if (maxHourlyCheckin > 0) {
                    actualRate = (double) actualCounts[d][hour] * 100.0 / (double) maxHourlyCheckin;
                }

                //Trộn lai
                int blendedVal = (int) Math.round((1.0 - alpha) * baseVal + alpha * actualRate);
                hoursData.add(blendedVal);
            }
            weeklyData.put(DAY_NAMES[d], hoursData);
        }

        return weeklyData;
    }

    /**
     * Đường cong bận rộn cơ bản dựa trên mô hình thực tế của trạm sạc xe điện.
     */
    private static double getBaseValue(int hour, int dayOfWeek) {
        boolean isWeekend = dayOfWeek >= 6;

        if (hour >= 23 || hour <= 4) {
            return 5.0;
        }
        if (hour <= 6) {
            return 15.0;
        }
        if (hour >= 7 && hour <= 9) {
            double peak = isWeekend ? 35.0 : 65.0;
            if (hour == 8) peak += 15.0;
            return peak;
        }
        if (hour >= 10 && hour <= 13) {
            return isWeekend ? 55.0 : 40.0;
        }
        if (hour >= 14 && hour <= 16) {
            return isWeekend ? 60.0 : 45.0;
        }
        if (hour >= 17 && hour <= 20) {
            double peak = isWeekend ? 55.0 : 75.0;
            if (hour == 18 || hour == 19) peak += 10.0;
            return peak;
        }
        return 25.0;
    }
}
