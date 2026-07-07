package com.vanquy.evcserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardStatsResponse {
    private long totalStations;
    private long totalUsers;
    private long totalReviews;
    private long totalVisits;
    private long totalCheckins;
    private List<DistrictStationCount> stationsByDistrict;
    private List<MonthlyReviewCount> reviewsByMonth;
    private List<CheckinStatusCount> checkinsByStatus;
    private List<MonthlyCheckinCount> checkinsByMonth;
    private List<TopCheckinStation> topCheckinStations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DistrictStationCount {
        private String district;
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyReviewCount {
        private String month;
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckinStatusCount {
        private String status;
        private String label;
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyCheckinCount {
        private String month;
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopCheckinStation {
        private Long stationId;
        private String stationName;
        private long checkinCount;
    }
}
