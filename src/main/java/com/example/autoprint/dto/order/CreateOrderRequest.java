package com.example.autoprint.dto.order;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String username;  // Using username instead of userId
    private List<DocumentInfo> documents;
    private Double totalAmount;
    private String currency = "INR";
    private String notes;
    
    @Data
    public static class DocumentInfo {
        private String documentId;
        private String fileName;
        private Integer pageCount;
        private Double price;
        private PrintSettings printSettings;
    }
    
    @Data
    public static class PrintSettings {
        private String sides; // SINGLE/DOUBLE
        private String color; // COLOR/BLACK_WHITE
        private String pageSize; // A4, A3, etc.
        private Integer copies;
        private String orientation; // PORTRAIT/LANDSCAPE
        private String pages; // e.g., "1-5,8,10-12"
    }
}
