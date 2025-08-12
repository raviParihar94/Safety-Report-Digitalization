package com.reportDigitalization.controller;

import com.factory.safety.entity.SafetyCheck;
import com.factory.safety.entity.User;
import com.factory.safety.enums.CheckStatus;
import com.factory.safety.enums.UserRole;
import com.factory.safety.service.SafetyCheckService;
import com.factory.safety.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SafetyCheckService safetyCheckService;

    // Check if user is admin
    private boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!isAdmin(user)) {
            return ResponseEntity.forbidden().body("Admin access required");
        }

        List<User> users = userService.findAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User newUser, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!isAdmin(user)) {
            return ResponseEntity.forbidden().body("Admin access required");
        }

        User created = userService.save(newUser);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!isAdmin(user)) {
            return ResponseEntity.forbidden().body("Admin access required");
        }

        // Implementation for toggling user active status
        return ResponseEntity.ok().body("User status updated");
    }

    @GetMapping("/safety-checks")
    public ResponseEntity<?> getAllSafetyChecks(HttpSession session,
                                                @RequestParam(required = false) String status) {
        User user = (User) session.getAttribute("user");
        if (!isAdmin(user)) {
            return ResponseEntity.forbidden().body("Admin access required");
        }

        // Get all safety checks or filter by status
        // Implementation depends on requirements
        return ResponseEntity.ok().body("Safety checks data");
    }

    @PutMapping("/safety-checks/{checkId}/approve")
    public ResponseEntity<?> approveSafetyCheck(@PathVariable Long checkId,
                                                @RequestBody Map<String, String> request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!isAdmin(user)) {
            return ResponseEntity.forbidden().body("Admin access required");
        }

        // Implementation for approving safety checks
        return ResponseEntity.ok().body("Safety check approved");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!isAdmin(user)) {
            return ResponseEntity.forbidden().body("Admin access required");
        }

        // Return admin dashboard data
        AdminDashboardDto dashboard = new AdminDashboardDto();
        dashboard.setTotalUsers(userService.findAllActiveUsers().size());
        dashboard.setPendingApprovals(0); // Calculate pending approvals
        dashboard.setTotalChecksThisMonth(0); // Calculate checks this month
        dashboard.setAverageScore(0.0); // Calculate overall average

        return ResponseEntity.ok(dashboard);
    }

    private static class AdminDashboardDto {
        public int totalUsers;
        public int pendingApprovals;
        public int totalChecksThisMonth;
        public double averageScore;

        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        public void setPendingApprovals(int pendingApprovals) { this.pendingApprovals = pendingApprovals; }
        public void setTotalChecksThisMonth(int totalChecksThisMonth) { this.totalChecksThisMonth = totalChecksThisMonth; }
        public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    }
}
