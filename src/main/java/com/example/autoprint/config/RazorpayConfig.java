package com.example.autoprint.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        // Basic validation for keys
        if (razorpayKeyId == null || razorpayKeyId.trim().isEmpty() || razorpayKeyId.equals("<YOUR_RAZORPAY_KEY_ID>")) {
            throw new IllegalArgumentException("Razorpay Key ID is not configured. Please set 'razorpay.key.id' in your application properties.");
        }
        if (razorpayKeySecret == null || razorpayKeySecret.trim().isEmpty() || razorpayKeySecret.equals("<YOUR_RAZORPAY_KEY_SECRET>")) {
            throw new IllegalArgumentException("Razorpay Key Secret is not configured. Please set 'razorpay.key.secret' in your application properties.");
        }
        return new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }
}
