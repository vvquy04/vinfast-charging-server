package com.vanquy.evcserver.util;

import com.vanquy.evcserver.dto.response.StationSummaryResponse;

import java.util.List;

/**
 * Lớp tiện ích tính toán thuật toán TOPSIS (Technique for Order of Preference
 * by Similarity to Ideal Solution) để xếp hạng các trạm sạc theo đa tiêu chí.
 *
 * 4 tiêu chí:
 *   C0: Distance (km)     — cost   (càng nhỏ càng tốt)
 *   C1: Power (kW)        — benefit (càng lớn càng tốt)
 *   C2: Occupancy (0-100) — cost   (càng nhỏ càng tốt)
 *   C3: Rating (0-5)      — benefit (càng lớn càng tốt)
 */
public class TopsisUtil {

    private static final int CRITERIA_COUNT = 4;

    /**
     * Tính Match Score cho từng trạm sạc bằng thuật toán TOPSIS.
     *
     * @param stations      Danh sách trạm sạc cần xếp hạng
     * @param wDistance      Trọng số tiêu chí khoảng cách
     * @param wPower         Trọng số tiêu chí công suất sạc
     * @param wOccupancy     Trọng số tiêu chí mức độ đông đúc
     * @param wRating        Trọng số tiêu chí điểm đánh giá
     */
    public static void calculateMatchScores(
            List<StationSummaryResponse> stations,
            double wDistance,
            double wPower,
            double wOccupancy,
            double wRating
    ) {
        if (stations == null || stations.isEmpty()) {
            return;
        }

        int m = stations.size();

        // Nếu chỉ có 1 trạm, gán 100%
        if (m == 1) {
            stations.get(0).setMatchScore(100);
            return;
        }

        // Xây dựng ma trận quyết định ──
        double[][] x = new double[m][CRITERIA_COUNT];
        for (int i = 0; i < m; i++) {
            StationSummaryResponse s = stations.get(i);

            // x0: Distance (km)
            x[i][0] = s.getDistance() != null ? s.getDistance() : 0.0;

            // x1: Max Power (kW) từ danh sách connector
            double maxPower = 0.0;
            if (s.getConnectorTypes() != null) {
                maxPower = s.getConnectorTypes().stream()
                        .mapToDouble(c -> c.getPowerKw() != null ? c.getPowerKw() : 0.0)
                        .max()
                        .orElse(0.0);
            }
            x[i][1] = maxPower;

            // x2: Occupancy (ánh xạ từ crowdStatus)
            x[i][2] = mapStatusToOccupancy(s.getCrowdStatus());

            // x3: Rating
            x[i][3] = s.getRating() != null ? s.getRating().doubleValue() : 0.0;
        }

        // Chuẩn hóa vector
        double[] colNorm = new double[CRITERIA_COUNT];
        for (int j = 0; j < CRITERIA_COUNT; j++) {
            double sumSq = 0.0;
            for (int i = 0; i < m; i++) {
                sumSq += x[i][j] * x[i][j];
            }
            colNorm[j] = Math.sqrt(sumSq);
        }

        double[][] r = new double[m][CRITERIA_COUNT];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < CRITERIA_COUNT; j++) {
                r[i][j] = colNorm[j] > 0.0 ? x[i][j] / colNorm[j] : 0.0;
            }
        }

        // Chuẩn hóa có trọng số ──
        double sumWeights = wDistance + wPower + wOccupancy + wRating;
        double[] weights = {
                wDistance / sumWeights,
                wPower / sumWeights,
                wOccupancy / sumWeights,
                wRating / sumWeights
        };

        double[][] v = new double[m][CRITERIA_COUNT];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < CRITERIA_COUNT; j++) {
                v[i][j] = r[i][j] * weights[j];
            }
        }

        //Xác định giải pháp lý tưởng (A+) và phản lý tưởng (A-)
        double[] aPlus = new double[CRITERIA_COUNT];
        double[] aMinus = new double[CRITERIA_COUNT];

        // x0: Distance — cost criterion (min = best)
        aPlus[0] = getMin(v, 0);
        aMinus[0] = getMax(v, 0);

        // x1: Power — benefit criterion (max = best)
        aPlus[1] = getMax(v, 1);
        aMinus[1] = getMin(v, 1);

        // x2: Occupancy — cost criterion (min = best)
        aPlus[2] = getMin(v, 2);
        aMinus[2] = getMax(v, 2);

        // x3: Rating — benefit criterion (max = best)
        aPlus[3] = getMax(v, 3);
        aMinus[3] = getMin(v, 3);

        //Tính khoảng cách Euclide và hệ số gần sát
        for (int i = 0; i < m; i++) {
            double sPlus = 0.0;
            double sMinus = 0.0;
            for (int j = 0; j < CRITERIA_COUNT; j++) {
                sPlus += Math.pow(v[i][j] - aPlus[j], 2);
                sMinus += Math.pow(v[i][j] - aMinus[j], 2);
            }
            sPlus = Math.sqrt(sPlus);
            sMinus = Math.sqrt(sMinus);

            double closeness;
            if (sPlus + sMinus > 0.0) {
                closeness = sMinus / (sPlus + sMinus);
            } else {
                closeness = 0.5;
            }

            double rawScore = closeness * 100.0;

            
            double distance = stations.get(i).getDistance() != null ? stations.get(i).getDistance() : 0.0;
            double penalty = 1.0;
            if (distance > 5.0) {
                // Giảm tuyến tính điểm số khi khoảng cách vượt quá 5km, về tối thiểu 10% tại mốc 20km
                penalty = Math.max(0.1, 1.0 - (distance - 5.0) / 15.0);
            }

            stations.get(i).setMatchScore((int) Math.round(rawScore * penalty));
        }
    }

    /**
     * Ánh xạ trạng thái crowdStatus sang giá trị occupancy số (0-100).
     */
    public static double mapStatusToOccupancy(String crowdStatus) {
        if (crowdStatus == null) return 10.0;
        return switch (crowdStatus) {
            case "MODERATE" -> 40.0;
            case "BUSY" -> 80.0;
            case "MAINTENANCE" -> 100.0;
            default -> 10.0; // EMPTY hoặc không xác định
        };
    }

    private static double getMax(double[][] v, int col) {
        double max = v[0][col];
        for (int i = 1; i < v.length; i++) {
            if (v[i][col] > max) max = v[i][col];
        }
        return max;
    }

    private static double getMin(double[][] v, int col) {
        double min = v[0][col];
        for (int i = 1; i < v.length; i++) {
            if (v[i][col] < min) min = v[i][col];
        }
        return min;
    }
}
