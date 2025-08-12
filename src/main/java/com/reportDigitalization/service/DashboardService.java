package com.reportDigitalization.service;

import com.factory.safety.entity.SafetyCheck;
import com.factory.safety.entity.User;
import com.factory.safety.dto.DashboardDto;
import com.factory.safety.enums.CheckStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private SafetyCheckService safetyCheckService;

    public DashboardDto getDashboardData(User user) {
        DashboardDto dashboard = new DashboardDto();

        dashboard.setUserName(user.getUsername());
        dashboard.setCompanyName(user.getCompanyName());
        dashboard.setRole(user.getRole().toString());
        dashboard.setTier(user.getTier() != null ? user.getTier().toString() : "N/A");

        List<SafetyCheck> userChecks = safetyCheckService.findByUser(user);

        // Last safety score
        if (!userChecks.isEmpty()) {
            SafetyCheck lastCheck = userChecks.get(0);
            dashboard.setLastSafetyScore(lastCheck.getPercentageScore());
            dashboard.setLastCheckDate(lastCheck.getCreatedAt());
        }

        // Average score
        Double avgScore = safetyCheckService.getAverageScoreByUser(user);
        dashboard.setAverageScore(avgScore != null ? avgScore : 0.0);

        // Total completed checks
        long completedChecks = userChecks.stream()
                .filter(check -> check.getStatus() == CheckStatus.COMPLETED ||
                        check.getStatus() == CheckStatus.APPROVED)
                .count();
        dashboard.setTotalChecksCompleted((int) completedChecks);

        // Pending checks
        long pendingChecks = userChecks.stream()
                .filter(check -> check.getStatus() == CheckStatus.PENDING ||
                        check.getStatus() == CheckStatus.IN_PROGRESS)
                .count();
        dashboard.setPendingChecks((int) pendingChecks);

        // Recent checks (last 5)
        List<DashboardDto.RecentCheckDto> recentChecks = userChecks.stream()
                .limit(5)
                .map(check -> new DashboardDto.RecentCheckDto(
                        check.getId(),
                        check.getCheckTitle(),
                        check.getFactoryName(),
                        check.getPercentageScore(),
                        check.getStatus().toString(),
                        check.getCreatedAt()
                ))
                .collect(Collectors.toList());
        dashboard.setRecentChecks(recentChecks);

        return dashboard;
    }
}
