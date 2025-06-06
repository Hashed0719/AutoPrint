package com.example.autoprint.dto.payment;

import com.example.autoprint.model.PrintOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResponse {
    private boolean success;
    private String message;
    private PrintOrder order;
    
    public static PaymentVerificationResponse success(PrintOrder order, String message) {
        return new PaymentVerificationResponse(true, message, order);
    }
    
    public static PaymentVerificationResponse error(String message) {
        return new PaymentVerificationResponse(false, message, null);
    }
}
