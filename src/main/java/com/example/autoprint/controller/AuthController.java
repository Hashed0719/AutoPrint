package com.example.autoprint.controller;

import com.example.autoprint.dto.AuthResponse;
import com.example.autoprint.dto.LoginRequest;
import com.example.autoprint.dto.RegisterRequest;
import com.example.autoprint.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Received login request for user: {}", request.getUsername());
            AuthResponse response = authService.authenticate(request);
            log.info("User {} successfully logged in", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login error for user {}: {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Received registration request for user: {}", request.getEmail());
            AuthResponse response = authService.register(request);
            log.info("Successfully registered user: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during registration for user {}: {}", request.getEmail(), e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error during registration: " + e.getMessage());
        }
    }
}
