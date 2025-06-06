package com.example.autoprint.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class PrintSettings {
    private String sides; // SINGLE/DOUBLE
    private String color; // COLOR/BLACK_WHITE
    private String pageSize; // A4, A3, etc.
    private Integer copies = 1;
    private String orientation; // PORTRAIT/LANDSCAPE
    private String pages; // e.g., "1-5,8,10-12"
}
