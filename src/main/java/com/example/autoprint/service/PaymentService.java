package com.example.autoprint.service;

import com.example.autoprint.dto.payment.VerifyPaymentRequest;
import com.example.autoprint.exception.PaymentVerificationException;
import com.example.autoprint.model.PrintOrder;
import com.example.autoprint.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    
    /**
     * Verify the payment signature and update the order status
     */
    @Transactional
    public PrintOrder verifyAndCapturePayment(VerifyPaymentRequest request) {
        try {
            // 1. Get the order from database
            PrintOrder order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new PaymentVerificationException("Order not found", HttpStatus.NOT_FOUND));
            
            // 2. Verify the payment signature
            boolean isValidSignature = verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
            );
            
            if (!isValidSignature) {
                throw new PaymentVerificationException("Invalid payment signature", HttpStatus.BAD_REQUEST);
            }
            
            // 3. Verify payment status with Razorpay
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            com.razorpay.Payment payment = razorpay.payments.fetch(request.getRazorpayPaymentId());
            
            if (!payment.has("status") || !"captured".equals(payment.get("status"))) {
                throw new PaymentVerificationException("Payment not captured", HttpStatus.BAD_REQUEST);
            }
            
            // 4. Update order status and payment details
            order.setPaymentId(request.getRazorpayPaymentId());
            order.setStatus(PrintOrder.OrderStatus.PAYMENT_RECEIVED);
            
            // Save the updated order
            return orderRepository.save(order);
            
        } catch (RazorpayException e) {
            log.error("Razorpay API error: {}", e.getMessage());
            throw new PaymentVerificationException("Error verifying payment with Razorpay: " + e.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Verify the payment signature using Razorpay's utility
     */
    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);
            
            return Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
        } catch (Exception e) {
            log.error("Error verifying payment signature: {}", e.getMessage());
            return false;
        }
    }
}
