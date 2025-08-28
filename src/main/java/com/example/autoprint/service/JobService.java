package com.example.autoprint.service;

import com.example.autoprint.model.PrintOrder;
import com.example.autoprint.model.User;
import com.example.autoprint.repository.OrderRepository;
import com.example.autoprint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PrintOrder> getPendingJobsForClient(String clientId) {
        return userRepository.findByUsername(clientId)
                .map(user -> orderRepository.findByUserAndStatus(user, PrintOrder.OrderStatus.PENDING_PAYMENT))
                .orElse(Collections.emptyList());
    }
    
    public List<PrintOrder> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<PrintOrder> getOrdersByUsername(String username) {
        return orderRepository.findByUserUsernameOrderByCreatedAtDesc(username);
    }
}
