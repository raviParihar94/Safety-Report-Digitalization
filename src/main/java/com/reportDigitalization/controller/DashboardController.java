package com.reportDigitalization.controller;

import com.factory.safety.dto.DashboardDto;
import com.factory.safety.entity.User;
import com.factory.safety.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<?> getDashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("Not authenticated");
        }

        DashboardDto dashboard = dashboardService.getDashboardData(user);
        return ResponseEntity.ok(dashboard);
    }
}
