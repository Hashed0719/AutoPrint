package com.example.autoprint.repository;

import com.example.autoprint.model.PrintOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<PrintOrder, Long> {
    Optional<PrintOrder> findByOrderNumber(String orderNumber);
    Optional<PrintOrder> findByRazorpayOrderId(String razorpayOrderId);
    Optional<PrintOrder> findByIdAndUserId(Long id, Long userId);
}
