package com.reportDigitalization.controller;

import com.factory.safety.entity.SafetyCheck;
import com.factory.safety.entity.User;
import com.factory.safety.service.SafetyCheckService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/safety-checks")
@CrossOrigin(origins = "*")
public class SafetyCheckController {

    @Autowired
    private SafetyCheckService safetyCheckService;

    @GetMapping
    public ResponseEntity<?> getUserSafetyChecks(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("Not authenticated");
        }

        List<SafetyCheck> checks = safetyCheckService.findByUser(user);
        return ResponseEntity.ok(checks);
    }

    @PostMapping
    public ResponseEntity<?> createSafetyCheck(@RequestBody SafetyCheck safetyCheck, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("Not authenticated");
        }

        safetyCheck.setUser(user);
        SafetyCheck created = safetyCheckService.createSafetyCheck(safetyCheck);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSafetyCheck(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("Not authenticated");
        }

        Optional<SafetyCheck> checkOpt = safetyCheckService.findById(id);
        if (checkOpt.isPresent()) {
            SafetyCheck check = checkOpt.get();
            // Ensure user can only access their own checks (or admin can access all)
            if (check.getUser().getId().equals(user.getId()) || user.getRole().toString().equals("ADMIN")) {
                return ResponseEntity.ok(check);
            } else {
                return ResponseEntity.forbidden().body("Access denied");
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<?> updateCheckItem(@PathVariable Long id, @PathVariable Long itemId,
                                             @RequestBody Map<String, Object> update, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("Not authenticated");
        }

        Double points = Double.valueOf(update.get("points").toString());
        String remarks = update.get("remarks").toString();

        SafetyCheck updated = safetyCheckService.updateCheckItem(itemId, points, remarks);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.badRequest().body("Update failed");
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<?> submitSafetyCheck(@PathVariable Long id,
                                               @RequestBody Map<String, String> request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("Not authenticated");
        }

        String comments = request.get("comments");
        SafetyCheck submitted = safetyCheckService.submitSafetyCheck(id, comments);
        if (submitted != null) {
            return ResponseEntity.ok(submitted);
        }
        return ResponseEntity.badRequest().body("Submission failed");
    }
}
```

        ```java
// AdminController.java
package com.factory.safety.controller;

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
```

        ## 8. Configuration Classes

```java
// SecurityConfig.java
package com.factory.safety.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }
}
