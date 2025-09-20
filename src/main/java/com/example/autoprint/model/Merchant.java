package com.example.autoprint.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String merchantNumber = "MER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // storing as Bcrypt hash

    @Column(nullable = false)
    private String shopName;

    @Column(nullable = false)
    private String shopAddress;
}