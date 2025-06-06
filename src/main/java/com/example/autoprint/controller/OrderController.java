package com.example.autoprint.controller;

import com.example.autoprint.dto.order.CreateOrderRequest;
import com.example.autoprint.model.PrintOrder;
import com.example.autoprint.model.User;
import com.example.autoprint.service.OrderService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("User not authenticated");
            }
            
            // Get the username from the authentication object
            String username = authentication.getName();
            
            // Set the username in the request
            request.setUsername(username);
            
            // Create order
            PrintOrder order = orderService.createOrder(request);
            
            // Create Razorpay order
            PrintOrder createdOrder = orderService.createRazorpayOrder(order);
            
            return ResponseEntity.ok(createdOrder);
            
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        }
    }
}
