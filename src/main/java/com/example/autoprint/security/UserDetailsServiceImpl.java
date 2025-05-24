package com.example.autoprint.security;

import com.example.autoprint.model.User;
import com.example.autoprint.repository.UserRepository;
import java.util.HashSet;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.info("Attempting to load user by username or email: {}", usernameOrEmail);
        
        // Try to find user by username first
        Optional<User> userByUsername = userRepository.findByUsername(usernameOrEmail);
        if (userByUsername.isPresent()) {
            User user = userByUsername.get();
            logUserDetails(user, "username");
            return createUserDetails(user);
        }
        
        // If not found by username, try to find by email
        Optional<User> userByEmail = userRepository.findByEmail(usernameOrEmail);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            logUserDetails(user, "email");
            return createUserDetails(user);
        }
        
        // If user not found by either username or email
        log.warn("User not found with username or email: {}", usernameOrEmail);
        throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
    }
    
    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new HashSet<>())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
    
    private void logUserDetails(User user, String loginMethod) {
        log.info("User found by {}: {}", loginMethod, user.getUsername());
        log.debug("User details - Username: {}, Email: {}, Active: {}", 
                 user.getUsername(), user.getEmail(), user.isActive());
    }
}
