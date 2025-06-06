package com.example.autoprint.dto.payment;

import lombok.Data;

@Data
public class VerifyPaymentRequest {
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
    private Long orderId;
}
