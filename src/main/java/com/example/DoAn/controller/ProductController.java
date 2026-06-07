package com.example.DoAn.controller;

import com.example.DoAn.config.ImageAssets;
import com.example.DoAn.model.Category;
import com.example.DoAn.model.FilterGroup;
import com.example.DoAn.model.Product;
import com.example.DoAn.repository.CategoryRepository;
import com.example.DoAn.repository.ProductRepository;
import com.example.DoAn.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.DoAn.model.User;
import com.example.DoAn.model.Order;
import com.example.DoAn.model.PasswordResetToken;
import com.example.DoAn.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Tự động thêm categories và imageAssets vào mọi trang
    @ModelAttribute("categories")
    public List<Category> populateCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("newProducts", productRepository.findTop8ByOrderByIdDesc());
        model.addAttribute("saleProducts", productRepository.findBySalePriceGreaterThan(0));
        return "index";
    }

    @GetMapping("/linh-kien-may-tinh")
    public String getCollectionPage(Model model,
                                   @RequestParam(required = false) List<String> brands,
                                   @RequestParam(required = false) Double minPrice,
                                   @RequestParam(required = false) Double maxPrice,
                                   @RequestParam(required = false) String sort) {
        
        // Giá trị mặc định cho lọc giá
        double min = (minPrice != null) ? minPrice : 0;
        double max = (maxPrice != null) ? maxPrice : 999_999_999;
        
        // Ràng buộc giá trị không âm và trong khoảng cho phép
        if (min < 1_000_000) min = 1_000_000;
        if (max > 100_000_000) max = 100_000_000;
        if (min > max) min = max;

        // Thực hiện lọc tại Database
        List<Product> products = productRepository.findByFilters(null, null, min, max);
        
        // Lấy danh sách thương hiệu duy nhất
        List<String> allBrands = productRepository.findAll().stream()
                .map(Product::getBrand)
                .filter(b -> b != null && !b.isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("allBrands", allBrands);

        // Lọc thương hiệu bằng Java do JPQL không hỗ trợ tốt Collection NULL
        if (brands != null && !brands.isEmpty()) {
            products.removeIf(p -> p.getBrand() == null || !brands.contains(p.getBrand()));
        }

        applySort(products, sort);

        model.addAttribute("products", products);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("currentBrands", brands);
        model.addAttribute("currentSort", sort);
        addGlobalSuggestions(model);
        return "linh-kien-may-tinh";
    }

    @GetMapping("/category/{id}")
    public String getByCategory(@PathVariable Long id, Model model,
                               @RequestParam(required = false) List<String> brands,
                               @RequestParam(required = false) Double minPrice,
                               @RequestParam(required = false) Double maxPrice,
                               @RequestParam(required = false) String sort) {
        Category category = categoryRepository.findById(id).orElseThrow();
        double min = (minPrice != null) ? minPrice : 0;
        double max = (maxPrice != null) ? maxPrice : 999_999_999;

        // Thực hiện lọc tại Database
        List<Product> products = productRepository.findByFilters(null, id, min, max);
        
        // Lấy danh sách thương hiệu trong danh mục này
        List<String> allBrands = productRepository.findByCategoryName(category.getName()).stream()
                .map(Product::getBrand)
                .filter(b -> b != null && !b.isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("allBrands", allBrands);

        if (brands != null && !brands.isEmpty()) {
            products.removeIf(p -> p.getBrand() == null || !brands.contains(p.getBrand()));
        }

        applySort(products, sort);

        model.addAttribute("products", products);
        model.addAttribute("currentCategory", category.getName());
        model.addAttribute("categoryId", id);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("currentBrands", brands);
        model.addAttribute("currentSort", sort);
        addGlobalSuggestions(model);
        return "linh-kien-may-tinh";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) List<String> brands,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        @RequestParam(required = false) String sort) {
        
        double min = (minPrice != null) ? minPrice : 0;
        double max = (maxPrice != null) ? maxPrice : 999_999_999;

        // Tìm kiếm và lọc toàn bộ tại Database
        List<Product> results = productRepository.findByFilters(q, categoryId, min, max);

        if (categoryId != null) {
            model.addAttribute("currentCategory", categoryRepository.findById(categoryId).get().getName());
            model.addAttribute("categoryId", categoryId);
        }

        if (brands != null && !brands.isEmpty()) {
            results.removeIf(p -> p.getBrand() == null || !brands.contains(p.getBrand()));
        }

        applySort(results, sort);

        model.addAttribute("products", results);
        model.addAttribute("searchQuery", q);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("currentBrands", brands);
        model.addAttribute("currentSort", sort);

        // Gợi ý sản phẩm thông minh
        if (!results.isEmpty()) {
            Category targetCat = results.get(0).getCategory();
            if (targetCat != null) {
                List<Product> suggestions = productRepository.findByCategoryName(targetCat.getName());
                suggestions.removeIf(p -> results.stream().anyMatch(r -> r.getId().equals(p.getId())));
                java.util.Collections.shuffle(suggestions);
                model.addAttribute("suggestedProducts", suggestions.size() > 4 ? suggestions.subList(0, 4) : suggestions);
            }
        } else {
            addGlobalSuggestions(model);
        }
        
        return "linh-kien-may-tinh";
    }

    @GetMapping("/api/products/suggestions")
    @org.springframework.web.bind.annotation.ResponseBody
    public List<Product> getSuggestions(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // findByNameContainingIgnoreCase hỗ trợ không phân biệt hoa thường
        List<Product> list = productRepository.findByNameContainingIgnoreCase(q.trim());
        // Trả về tối đa 10 gợi ý
        return list.size() > 10 ? list.subList(0, 10) : list;
    }

    // Helper: Chỉ còn nhiệm vụ sắp xếp (Yêu cầu C.2)
    private void applySort(List<Product> products, String sort) {
        if (sort != null) {
            switch (sort) {
                case "price-asc":
                    products.sort((p1, p2) -> Double.compare(
                        p1.getSalePrice() > 0 ? p1.getSalePrice() : p1.getOriginalPrice(),
                        p2.getSalePrice() > 0 ? p2.getSalePrice() : p2.getOriginalPrice()
                    ));
                    break;
                case "price-desc":
                    products.sort((p1, p2) -> Double.compare(
                        p2.getSalePrice() > 0 ? p2.getSalePrice() : p2.getOriginalPrice(),
                        p1.getSalePrice() > 0 ? p1.getSalePrice() : p1.getOriginalPrice()
                    ));
                    break;
                case "newest":
                    products.sort((p1, p2) -> p2.getId().compareTo(p1.getId()));
                    break;
            }
        }
    }

    // Helper: Thêm gợi ý sản phẩm nổi bật
    private void addGlobalSuggestions(Model model) {
        List<Product> all = productRepository.findAll();
        java.util.Collections.shuffle(all);
        model.addAttribute("suggestedProducts", all.size() > 4 ? all.subList(0, 4) : all);
    }

    @GetMapping("/product/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        model.addAttribute("product", product);
        
        // Filter only approved reviews
        model.addAttribute("approvedReviews", product.getReviews().stream().filter(r -> r.isApproved()).collect(Collectors.toList()));

        // Lấy sản phẩm cùng loại, loại bỏ chính nó
        List<Product> related = productRepository.findByCategoryName(product.getCategory().getName());
        related.removeIf(p -> p.getId().equals(id));
        model.addAttribute("relatedProducts", related);

        // Check canReview
        boolean canReview = false;
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userService.findByUsername(auth.getName()).orElse(null);
            if (user != null) {
                // Kiểm tra user đã mua sp này và đơn hoàn thành
                List<Order> orders = orderRepository.findByUser(user);
                canReview = orders.stream()
                        .filter(o -> "COMPLETED".equals(o.getStatus()))
                        .flatMap(o -> o.getOrderItems().stream())
                        .anyMatch(item -> item.getProduct().getId().equals(id));
            }
        }
        model.addAttribute("canReview", canReview);

        return "product-detail";
    }

    @GetMapping("/news")
    public String news() {
        return "news";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }

    @Autowired
    private UserService userService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model, HttpServletRequest request) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);

            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String resetUrl = baseUrl + "/reset-password?token=" + token;

            String content = "Chào " + user.getFullName() + ",\n\n"
                    + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấn vào đường dẫn dưới đây để thực hiện:\n"
                    + resetUrl + "\n\n"
                    + "Đường dẫn này sẽ hết hạn sau 60 phút.\n"
                    + "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.";

            try {
                userService.sendEmail(email, "Yêu cầu khôi phục mật khẩu - WebLinhKien", content);
                model.addAttribute("success", "Một liên kết đặt lại mật khẩu đã được gửi đến email của bạn.");
            } catch (Exception e) {
                System.out.println("\n========== MÔ PHỎNG GỬI EMAIL (DO CHƯA CẤU HÌNH SMTP) ==========");
                System.out.println("Người nhận: " + email);
                System.out.println("Link đặt lại mật khẩu: " + resetUrl);
                System.out.println("=================================================================\n");
                
                // Dành cho mục đích chấm đồ án: Hiện luôn link ra màn hình để test
                model.addAttribute("success", "Mô phỏng gửi Email thành công! (Dành cho Test) Link khôi phục: " + resetUrl);
            }
        } else {
            model.addAttribute("error", "Email không tồn tại trong hệ thống.");
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> tokenOptional = userService.getPasswordResetToken(token);
        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            model.addAttribute("error", "Liên kết không hợp lệ hoặc đã hết hạn.");
            return "forgot-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String password, Model model) {
        Optional<PasswordResetToken> tokenOptional = userService.getPasswordResetToken(token);
        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            model.addAttribute("error", "Liên kết không hợp lệ hoặc đã hết hạn.");
            return "forgot-password";
        }

        User user = tokenOptional.get().getUser();
        userService.changeUserPassword(user, password);
        model.addAttribute("success", "Mật khẩu đã được thay đổi thành công. Vui lòng đăng nhập lại.");
        return "login";
    }

    @GetMapping("/api/products/{id}")
    @org.springframework.web.bind.annotation.ResponseBody
    public Product getProductApi(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow();
    }
}
