package com.example.autoprint.repository;

import com.example.autoprint.model.PrintOrder;
import com.example.autoprint.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<PrintOrder, Long> {
    List<PrintOrder> findByUserAndStatus(User user, PrintOrder.OrderStatus status);
    Optional<PrintOrder> findByOrderNumber(String orderNumber);
    Optional<PrintOrder> findByRazorpayOrderId(String razorpayOrderId);
    Optional<PrintOrder> findByIdAndUserId(Long id, Long userId);
}
