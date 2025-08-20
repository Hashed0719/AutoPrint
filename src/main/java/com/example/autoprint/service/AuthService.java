package com.example.autoprint.service;

import com.example.autoprint.dto.AuthResponse;
import com.example.autoprint.dto.LoginRequest;
import com.example.autoprint.dto.RegisterRequest;
import org.springframework.security.core.AuthenticationException;
import com.example.autoprint.exception.UserAlreadyExistsException;
import com.example.autoprint.model.User;
import com.example.autoprint.repository.UserRepository;
import com.example.autoprint.security.JwtService;
import com.example.autoprint.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthResponse authenticate(LoginRequest request) {
        log.info("Starting authentication for: {}", request.getUsername());
        try {
            var userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            log.debug("Verifying password for user: {}", request.getUsername());
            if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                log.warn("Password verification failed for: {}", request.getUsername());
                throw new org.springframework.security.authentication.BadCredentialsException("Invalid username/email or password");
            }
            log.debug("Password verified successfully for user: {}", request.getUsername());
            
            log.debug("Generating JWT token for user: {}", request.getUsername());
            String token = jwtService.generateToken(userDetails);
            log.info("JWT token generated successfully for user: {}", request.getUsername());
            
            // Get the username from the authenticated user details (which will be the actual username, not the email)
            String authenticatedUsername = userDetails.getUsername();
            
            log.debug("Fetching additional user details for: {}", authenticatedUsername);
            var user = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> {
                    log.error("User not found in database after successful authentication: {}", authenticatedUsername);
                    return new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found");
                });
            
            log.info("Authentication successful for user: {}", authenticatedUsername);
            return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .message("Login successful")
                .build();
                
        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Authentication failed for user {}: {}", request.getUsername(), e.getMessage(), e);
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid username or password", e);
        }
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Starting registration process for user: {}", request.getEmail());
        
        try {
            // Validate request
            if (request == null || !StringUtils.hasText(request.getEmail()) || 
                !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
                throw new IllegalArgumentException("Invalid registration request: missing required fields");
            }

            log.debug("Checking if username '{}' already exists", request.getUsername());
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("Username is already taken!");
            }

            log.debug("Checking if email '{}' already exists", request.getEmail());
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email is already in use!");
            }

            // Create new user
            log.debug("Creating new user with username: {}", request.getUsername());
            User user = new User();
            user.setUsername(request.getUsername().trim());
            user.setEmail(request.getEmail().trim().toLowerCase());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setActive(true);
            
            // Save user to database
            User savedUser = userRepository.save(user);
            log.info("User {} saved successfully with ID: {}", savedUser.getUsername(), savedUser.getId());
            
            // Generate JWT token
            var userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
            String token = jwtService.generateToken(userDetails);
            log.debug("JWT token generated for user: {}", savedUser.getUsername());
            
            return AuthResponse.builder()
                    .token(token)
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .message("User registered successfully")
                    .build();
                    
        } catch (UserAlreadyExistsException e) {
            log.warn("Registration failed - {}", e.getMessage());
            throw e; // Re-throw to be handled by the controller
        } catch (Exception e) {
            log.error("Unexpected error during registration for user {}: {}", 
                     request != null ? request.getEmail() : "unknown", e.getMessage(), e);
            throw new RuntimeException("Registration failed due to an unexpected error", e);
        }
    }
}
