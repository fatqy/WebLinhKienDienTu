package com.example.DoAn.controller;

import com.example.DoAn.model.*;
import com.example.DoAn.repository.*;
import com.example.DoAn.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        model.addAttribute("revenue", adminService.calculateTotalRevenue());
        model.addAttribute("monthlyRevenue", adminService.calculateRevenueByMonth(now.getMonthValue(), now.getYear()));
        model.addAttribute("dailyRevenue", adminService.calculateRevenueByDay(now));
        model.addAttribute("newOrdersCount", adminService.countNewOrders());
        model.addAttribute("customerCount", adminService.countTotalCustomers());
        model.addAttribute("recentOrders", adminService.getRecentOrders());
        
        // Cảnh báo sản phẩm sắp hết hàng (< 5)
        List<Product> lowStockProducts = productRepository.findAll().stream()
                .filter(p -> p.getStock() < 5)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("lowStockProducts", lowStockProducts);
        
        return "admin/dashboard";
    }

    // --- QUẢN LÝ DANH MỤC ---
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam String name) {
        categoryRepository.save(new Category(name));
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/edit")
    public String editCategory(@RequestParam Long id, @RequestParam String name) {
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setName(name);
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String deleteCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        
        // Tìm tất cả sản phẩm thuộc danh mục này và gỡ bỏ liên kết
        List<Product> products = productRepository.findByCategoryName(category.getName());
        for (Product p : products) {
            p.setCategory(null);
            productRepository.save(p);
        }
        
        categoryRepository.delete(category);
        return "redirect:/admin/categories";
    }

    // --- QUẢN LÝ SẢN PHẨM ---
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/products";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam Long categoryId) {
        product.setCategory(categoryRepository.findById(categoryId).get());
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/edit")
    public String editProduct(@ModelAttribute Product product, @RequestParam Long categoryId) {
        Product existingProduct = productRepository.findById(product.getId()).orElseThrow();
        existingProduct.setName(product.getName());
        existingProduct.setOriginalPrice(product.getOriginalPrice());
        existingProduct.setSalePrice(product.getSalePrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setExtraImages(product.getExtraImages());
        existingProduct.setBadge(product.getBadge());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setCategory(categoryRepository.findById(categoryId).get());
        
        productRepository.save(existingProduct);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }

    // --- QUẢN LÝ ĐƠN HÀNG ---
    @GetMapping("/orders")
    public String listOrders(Model model,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate) {
        List<Order> orders = orderRepository.findAll();

        // Lọc theo từ khóa (tên khách hàng hoặc số điện thoại)
        if (keyword != null && !keyword.isEmpty()) {
            orders.removeIf(o -> (o.getFullName() == null || !o.getFullName().toLowerCase().contains(keyword.toLowerCase())) &&
                                 (o.getPhoneNumber() == null || !o.getPhoneNumber().contains(keyword)));
            model.addAttribute("keyword", keyword);
        }

        // Lọc theo ngày (giả định định dạng yyyy-MM-dd)
        if (startDate != null && !startDate.isEmpty()) {
            java.time.LocalDateTime start = java.time.LocalDate.parse(startDate).atStartOfDay();
            orders.removeIf(o -> o.getOrderDate().isBefore(start));
            model.addAttribute("startDate", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            java.time.LocalDateTime end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
            orders.removeIf(o -> o.getOrderDate().isAfter(end));
            model.addAttribute("endDate", endDate);
        }

        // Đảo ngược danh sách để đơn hàng mới nhất lên đầu
        java.util.Collections.reverse(orders);

        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin/orders";
    }

    // --- QUẢN LÝ NGƯỜI DÙNG ---
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // --- QUẢN LÝ ĐÁNH GIÁ ---
    @GetMapping("/reviews")
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll());
        return "admin/reviews";
    }

    @PostMapping("/reviews/delete")
    public String deleteReview(@RequestParam Long reviewId) {
        reviewRepository.deleteById(reviewId);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/approve")
    public String approveReview(@RequestParam Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        review.setApproved(true);
        reviewRepository.save(review);
        return "redirect:/admin/reviews";
    }
}
