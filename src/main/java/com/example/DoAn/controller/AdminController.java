package com.example.DoAn.controller;

import com.example.DoAn.model.*;
import com.example.DoAn.repository.*;
import com.example.DoAn.service.AdminService;
import com.example.DoAn.service.GeminiAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private GeminiAIService geminiAIService;

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

    @Autowired
    private com.example.DoAn.service.AuditLogService auditLogService;

    @Autowired
    private com.example.DoAn.repository.RoleRepository roleRepository;

    @Autowired
    private com.example.DoAn.repository.OrderItemRepository orderItemRepository;

    @Autowired
    private com.example.DoAn.repository.CartItemRepository cartItemRepository;

    @Autowired
    private com.example.DoAn.repository.CouponRepository couponRepository;

    // --- QUẢN LÝ MÃ GIẢM GIÁ (COUPONS) ---
    @GetMapping("/coupons")
    public String listCoupons(Model model) {
        model.addAttribute("coupons", couponRepository.findAll());
        return "admin/coupons";
    }

    @PostMapping("/coupons/add")
    public String addCoupon(@ModelAttribute Coupon coupon, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            couponRepository.save(coupon);
            auditLogService.logAction("ADD", "COUPON", coupon.getCode(), "Thêm mã giảm giá mới: " + coupon.getCode());
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Mã giảm giá có thể đã tồn tại!");
        }
        return "redirect:/admin/coupons";
    }

    @PostMapping("/coupons/edit")
    public String editCoupon(@ModelAttribute Coupon coupon, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            Coupon existing = couponRepository.findById(coupon.getId()).orElseThrow();
            existing.setCode(coupon.getCode());
            existing.setDiscountAmount(coupon.getDiscountAmount());
            existing.setDiscountType(coupon.getDiscountType());
            existing.setMinOrderValue(coupon.getMinOrderValue());
            existing.setExpiryDate(coupon.getExpiryDate());
            existing.setActive(coupon.isActive());
            couponRepository.save(existing);
            auditLogService.logAction("UPDATE", "COUPON", coupon.getCode(), "Cập nhật mã giảm giá: " + coupon.getCode());
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/coupons";
    }

    @GetMapping("/coupons/delete/{id}")
    public String deleteCoupon(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Coupon coupon = couponRepository.findById(id).orElse(null);
        if (coupon != null) {
            couponRepository.delete(coupon);
            auditLogService.logAction("DELETE", "COUPON", coupon.getCode(), "Xóa mã giảm giá: " + coupon.getCode());
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa mã giảm giá!");
        }
        return "redirect:/admin/coupons";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        model.addAttribute("revenue", adminService.calculateTotalRevenue());
        model.addAttribute("monthlyRevenue", adminService.calculateRevenueByMonth(now.getMonthValue(), now.getYear()));
        model.addAttribute("dailyRevenue", adminService.calculateRevenueByDay(now));
        model.addAttribute("newOrdersCount", adminService.countNewOrders());
        model.addAttribute("customerCount", adminService.countTotalCustomers());
        model.addAttribute("recentOrders", adminService.getRecentOrders());
        
        model.addAttribute("chartLabels", adminService.getLabelsLast6Months());
        model.addAttribute("chartData", adminService.getRevenueLast6Months());
        
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
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống!");
        }
        categoryRepository.save(new Category(name));
        auditLogService.logAction("ADD", "CATEGORY", name, "Thêm danh mục mới: " + name);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/edit")
    public String editCategory(@RequestParam Long id, @RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống!");
        }
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setName(name);
        categoryRepository.save(category);
        auditLogService.logAction("UPDATE", "CATEGORY", id.toString(), "Cập nhật danh mục thành: " + name);
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
        auditLogService.logAction("DELETE", "CATEGORY", id.toString(), "Xóa danh mục: " + category.getName());
        return "redirect:/admin/categories";
    }

    private String sanitizeHtml(String html) {
        if (html == null) return null;
        // Sử dụng Jsoup để lọc HTML an toàn (Yêu cầu QA: TC-A02)
        return Jsoup.clean(html, Safelist.basic());
    }

    // --- QUẢN LÝ SẢN PHẨM ---
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/products";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, 
                             @RequestParam Long categoryId,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             @RequestParam(value = "extraImageFiles", required = false) MultipartFile[] extraImageFiles) {
        validateProduct(product);
        product.setDescription(sanitizeHtml(product.getDescription()));
        product.setCategory(categoryRepository.findById(categoryId).get());
        
        // Lưu ảnh chính
        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageUrl("/uploads/" + saveImage(imageFile));
        }
        
        // Lưu ảnh phụ
        if (extraImageFiles != null && extraImageFiles.length > 0) {
            List<String> extraPaths = new ArrayList<>();
            for (MultipartFile file : extraImageFiles) {
                if (!file.isEmpty()) {
                    extraPaths.add("/uploads/" + saveImage(file));
                }
            }
            if (!extraPaths.isEmpty()) {
                product.setExtraImages(String.join(",", extraPaths));
            }
        }

        productRepository.save(product);
        auditLogService.logAction("ADD", "PRODUCT", product.getId().toString(), "Thêm sản phẩm mới: " + product.getName());
        return "redirect:/admin/products";
    }

    @PostMapping("/products/edit")
    public String editProduct(@ModelAttribute Product product, 
                              @RequestParam Long categoryId,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestParam(value = "extraImageFiles", required = false) MultipartFile[] extraImageFiles) {
        validateProduct(product);
        Product existingProduct = productRepository.findById(product.getId()).orElseThrow();
        existingProduct.setName(product.getName());
        existingProduct.setOriginalPrice(product.getOriginalPrice());
        existingProduct.setSalePrice(product.getSalePrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setBadge(product.getBadge());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setDescription(sanitizeHtml(product.getDescription()));
        existingProduct.setCategory(categoryRepository.findById(categoryId).get());
        
        if (imageFile != null && !imageFile.isEmpty()) {
            existingProduct.setImageUrl("/uploads/" + saveImage(imageFile));
        } else if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            existingProduct.setImageUrl(product.getImageUrl());
        }

        if (extraImageFiles != null && extraImageFiles.length > 0 && !extraImageFiles[0].isEmpty()) {
            List<String> extraPaths = new ArrayList<>();
            for (MultipartFile file : extraImageFiles) {
                if (!file.isEmpty()) {
                    extraPaths.add("/uploads/" + saveImage(file));
                }
            }
            if (!extraPaths.isEmpty()) {
                existingProduct.setExtraImages(String.join(",", extraPaths));
            }
        } else if (product.getExtraImages() != null) {
            existingProduct.setExtraImages(product.getExtraImages());
        }
        
        productRepository.save(existingProduct);
        auditLogService.logAction("UPDATE", "PRODUCT", existingProduct.getId().toString(), "Cập nhật sản phẩm: " + existingProduct.getName());
        return "redirect:/admin/products";
    }

    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = "./uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, file.getBytes());
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên sản phẩm không được để trống!");
        }
        if (product.getOriginalPrice() < 0) {
            throw new RuntimeException("Giá gốc không được nhỏ hơn 0đ!");
        }
        if (product.getSalePrice() < 0) {
            product.setSalePrice(0);
        }
        if (product.getSalePrice() > product.getOriginalPrice()) {
            throw new RuntimeException("Giá khuyến mãi phải nhỏ hơn hoặc bằng giá gốc!");
        }
        if (product.getStock() < 0) {
            throw new RuntimeException("Số lượng tồn kho không được nhỏ hơn 0!");
        }
    }

    @GetMapping("/products/delete/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String deleteProduct(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            Product p = productRepository.findById(id).orElse(null);
            if(p != null) {
                String pName = p.getName();
                
                // Xóa luôn: xóa tất cả các giỏ hàng và chi tiết đơn hàng đang chứa sản phẩm này
                cartItemRepository.deleteByProductId(id);
                orderItemRepository.deleteByProductId(id);
                
                productRepository.deleteById(id);
                auditLogService.logAction("DELETE", "PRODUCT", id.toString(), "Xóa sản phẩm (ép xóa): " + pName);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công (đã gỡ khỏi các đơn hàng cũ)!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi xóa sản phẩm: " + e.getMessage());
        }
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
        String currentStatus = order.getStatus();

        if (currentStatus.equals(status)) {
            return "redirect:/admin/orders";
        }



        order.setStatus(status);
        orderRepository.save(order);
        auditLogService.logAction("UPDATE", "ORDER", orderId.toString(), "Cập nhật trạng thái đơn hàng thành: " + status);
        return "redirect:/admin/orders";
    }

    // --- QUẢN LÝ NGƯỜI DÙNG ---
    @GetMapping("/users")
    public String listUsers(Model model) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        
        List<User> users = userRepository.findAll();
        if (!isSuperAdmin) {
            // Ẩn tất cả các tài khoản có quyền SUPER_ADMIN khỏi mắt của Admin thường
            users.removeIf(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_SUPER_ADMIN")));
        }
        
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName().equals(user.getUsername())) {
            return "redirect:/admin/users"; // Không thể tự khóa mình
        }
        if (user.getUsername().equals("superadmin")) {
            return "redirect:/admin/users"; // Không ai được khóa superadmin
        }

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        auditLogService.logAction("UPDATE", "USER", userId.toString(), "Thay đổi trạng thái tài khoản thành: " + (user.isEnabled() ? "Kích hoạt" : "Khóa"));
        return "redirect:/admin/users";
    }

    @PostMapping("/users/assign-role")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('SUPER_ADMIN')")
    public String assignRole(@RequestParam Long userId, @RequestParam Long roleId, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // Không cho phép thay đổi quyền của tài khoản root (superadmin)
        if ("superadmin".equals(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Không thể thay đổi quyền của tài khoản root.");
            return "redirect:/admin/users";
        }
        
        Role role = roleRepository.findById(roleId).orElseThrow();
        
        user.setRoles(java.util.Collections.singleton(role));
        userRepository.save(user);
        auditLogService.logAction("UPDATE", "USER", userId.toString(), "Cập nhật quyền thành: " + role.getName());
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
        auditLogService.logAction("DELETE", "REVIEW", reviewId.toString(), "Xóa đánh giá của khách hàng");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/toggle")
    public String toggleReview(@RequestParam Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        review.setApproved(!review.isApproved());
        reviewRepository.save(review);
        auditLogService.logAction("UPDATE", "REVIEW", reviewId.toString(), "Đổi trạng thái hiển thị đánh giá thành: " + (review.isApproved() ? "Hiện" : "Ẩn"));
        return "redirect:/admin/reviews";
    }

    // --- BÁO CÁO & THỐNG KÊ ---
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("revenue", adminService.calculateTotalRevenue());
        model.addAttribute("customerCount", adminService.countTotalCustomers());
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        return "admin/reports";
    }

    // --- CÀI ĐẶT HỆ THỐNG ---
    @GetMapping("/settings")
    public String settings(Model model) {
        return "admin/settings";
    }

    // --- NHẬT KÝ HỆ THỐNG (AUDIT LOGS) ---
    @GetMapping("/audit-logs")
    public String auditLogs(Model model) {
        model.addAttribute("logs", auditLogService.getAllLogs());
        return "admin/audit-logs";
    }

    // --- API TÍCH HỢP AI ---
    @PostMapping("/api/generate-description")
    @ResponseBody
    public Mono<String> generateDescription(@RequestParam String name, @RequestParam(required = false) String categoryName, @RequestParam(required = false) String brand) {
        return geminiAIService.generateProductDescription(name, categoryName, brand);
    }
}
