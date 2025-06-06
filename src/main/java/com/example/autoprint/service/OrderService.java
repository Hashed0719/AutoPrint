package com.example.autoprint.service;

import com.example.autoprint.dto.order.CreateOrderRequest;
import com.example.autoprint.exception.ResourceNotFoundException;
import com.example.autoprint.model.*;
import com.example.autoprint.repository.OrderRepository;
import com.example.autoprint.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RazorpayClient razorpayClient;
    
    @Value("${razorpay.currency:INR}")
    private String currency;
    
    @Value("${razorpay.receipt.prefix:order_rcpt_}")
    private String receiptPrefix;
    
    @Transactional
    public PrintOrder createOrder(CreateOrderRequest request) {
        // Validate request
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + request.getUsername()));
        
        // Create order
        PrintOrder order = new PrintOrder();
        order.setUser(user);
        // Convert paise to rupees before storing in database
        order.setTotalAmount(request.getTotalAmount() / 100.0);
        order.setCurrency(currency);
        order.setNotes(request.getNotes());
        
        // Add order items
        List<OrderItem> items = request.getDocuments().stream()
                .map(doc -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setDocumentId(doc.getDocumentId());
                    item.setFileName(doc.getFileName());
                    item.setPageCount(doc.getPageCount());
                    item.setPrice(doc.getPrice());
                    
                    // Set print settings
                    PrintSettings settings = new PrintSettings();
                    settings.setSides(doc.getPrintSettings().getSides());
                    settings.setColor(doc.getPrintSettings().getColor());
                    settings.setPageSize(doc.getPrintSettings().getPageSize());
                    settings.setCopies(doc.getPrintSettings().getCopies());
                    settings.setOrientation(doc.getPrintSettings().getOrientation());
                    settings.setPages(doc.getPrintSettings().getPages());
                    
                    item.setPrintSettings(settings);
                    return item;
                })
                .collect(Collectors.toList());
        
        order.setItems(items);
        
        // Save order to get the ID
        PrintOrder savedOrder = orderRepository.save(order);
        log.info("Created order with ID: {}", savedOrder.getId());
        
        return savedOrder;
    }
    
    @Transactional
    public PrintOrder createRazorpayOrder(PrintOrder order) throws RazorpayException {
        try {
            // Create Razorpay order
            JSONObject orderRequest = new JSONObject();
            // Convert rupees back to paise for Razorpay
            orderRequest.put("amount", (int)(order.getTotalAmount() * 100));
            orderRequest.put("currency", order.getCurrency());
            orderRequest.put("receipt", receiptPrefix + order.getOrderNumber());
            orderRequest.put("payment_capture", 1); // Auto-capture payment
            
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            log.info("Created Razorpay order: {}", razorpayOrder.toString());
            
            // Update order with Razorpay order ID
            order.setRazorpayOrderId(razorpayOrder.get("id"));
            order.setStatus(PrintOrder.OrderStatus.PENDING_PAYMENT);
            return orderRepository.save(order);
            
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order: {}", e.getMessage(), e);
            throw new RazorpayException("Failed to create Razorpay order: " + e.getMessage());
        }
    }
}
