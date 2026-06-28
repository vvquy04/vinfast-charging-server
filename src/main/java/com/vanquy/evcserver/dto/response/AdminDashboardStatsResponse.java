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
    private List<DistrictStationCount> stationsByDistrict;
    private List<MonthlyReviewCount> reviewsByMonth;

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
}
