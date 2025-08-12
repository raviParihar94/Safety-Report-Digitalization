package com.reportDigitalization.controller;

import com.factory.safety.dto.LoginRequest;
import com.factory.safety.entity.User;
import com.factory.safety.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userService.validatePassword(loginRequest.getPassword(), user.getPassword())) {
                if (user.isActive()) {
                    // Update last login
                    userService.updateLastLogin(user);

                    // Store user in session
                    session.setAttribute("user", user);

                    return ResponseEntity.ok().body(new LoginResponse("Login successful", user.getRole().toString()));
                } else {
                    return ResponseEntity.badRequest().body(new LoginResponse("Account is inactive", null));
                }
            }
        }

        return ResponseEntity.badRequest().body(new LoginResponse("Invalid credentials", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().body(new LoginResponse("Logged out successfully", null));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().body("Not authenticated");
    }

    private static class LoginResponse {
        public String message;
        public String role;

        public LoginResponse(String message, String role) {
            this.message = message;
            this.role = role;
        }
    }
}
