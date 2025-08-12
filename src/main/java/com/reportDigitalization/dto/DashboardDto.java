package com.reportDigitalization.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDto {
    private String userName;
    private String companyName;
    private String role;
    private String tier;
    private Double lastSafetyScore;
    private LocalDateTime lastCheckDate;
    private Double averageScore;
    private int totalChecksCompleted;
    private int pendingChecks;
    private List<RecentCheckDto> recentChecks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentCheckDto {
        private Long id;
        private String title;
        private String factoryName;
        private Double score;
        private String status;
        private LocalDateTime date;
    }
}
