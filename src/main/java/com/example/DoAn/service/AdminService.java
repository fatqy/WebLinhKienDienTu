package com.example.DoAn.service;

import com.example.DoAn.model.Order;
import com.example.DoAn.repository.OrderRepository;
import com.example.DoAn.repository.ProductRepository;
import com.example.DoAn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public double calculateTotalRevenue() {
        return orderRepository.findAll().stream()
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public long countNewOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> "PENDING".equals(order.getStatus()))
                .count();
    }

    public long countTotalCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> "ROLE_USER".equals(role.getName())))
                .count();
    }

    public List<Order> getRecentOrders() {
        return orderRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
    }
}
