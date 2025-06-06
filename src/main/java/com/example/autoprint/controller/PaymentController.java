package com.example.autoprint.controller;

import com.example.autoprint.dto.payment.PaymentVerificationResponse;
import com.example.autoprint.dto.payment.VerifyPaymentRequest;
import com.example.autoprint.model.PrintOrder;
import com.example.autoprint.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {
        try {
            log.info("Verifying payment for order: {}", request.getOrderId());
            
            // Verify and capture payment
            PrintOrder order = paymentService.verifyAndCapturePayment(request);
            
            log.info("Payment verified successfully for order: {}", order.getId());
            
            return ResponseEntity.ok(
                PaymentVerificationResponse.success(
                    order,
                    "Payment verified successfully"
                )
            );
            
        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            
            return ResponseEntity.badRequest().body(
                PaymentVerificationResponse.error(
                    e.getMessage() != null ? e.getMessage() : "Payment verification failed"
                )
            );
        }
    }
}
