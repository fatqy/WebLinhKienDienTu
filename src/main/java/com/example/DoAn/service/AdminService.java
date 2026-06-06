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

    public double calculateRevenueByMonth(int month, int year) {
        return orderRepository.findAll().stream()
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .filter(order -> order.getOrderDate().getMonthValue() == month && order.getOrderDate().getYear() == year)
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public double calculateRevenueByDay(LocalDateTime date) {
        return orderRepository.findAll().stream()
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .filter(order -> order.getOrderDate().toLocalDate().equals(date.toLocalDate()))
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

    public List<Double> getRevenueLast6Months() {
        List<Double> revenues = new java.util.ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDateTime monthDate = now.minusMonths(i);
            revenues.add(calculateRevenueByMonth(monthDate.getMonthValue(), monthDate.getYear()));
        }
        return revenues;
    }

    public List<String> getLabelsLast6Months() {
        List<String> labels = new java.util.ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDateTime monthDate = now.minusMonths(i);
            labels.add("Tháng " + monthDate.getMonthValue());
        }
        return labels;
    }
}
