package com.example.DoAn.controller;

import com.example.DoAn.config.ImageAssets;
import com.example.DoAn.model.Category;
import com.example.DoAn.model.FilterGroup;
import com.example.DoAn.model.Product;
import com.example.DoAn.repository.CategoryRepository;
import com.example.DoAn.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.DoAn.model.User;
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
        List<Product> products = productRepository.findAll();
        
        // Lấy danh sách thương hiệu duy nhất (trước khi lọc)
        List<String> allBrands = products.stream()
                .map(Product::getBrand)
                .filter(b -> b != null && !b.isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("allBrands", allBrands);

        applyFiltersAndSort(products, brands, minPrice, maxPrice, sort);

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
        List<Product> products = new ArrayList<>(category.getProducts());
        
        // Lấy danh sách thương hiệu trong danh mục này
        List<String> allBrands = products.stream()
                .map(Product::getBrand)
                .filter(b -> b != null && !b.isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("allBrands", allBrands);

        applyFiltersAndSort(products, brands, minPrice, maxPrice, sort);

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

    @GetMapping("/api/products/suggestions")
    @org.springframework.web.bind.annotation.ResponseBody
    public List<Product> getSuggestions(@RequestParam String q) {
        List<Product> list = productRepository.findByNameContainingIgnoreCase(q);
        // Trả về tối đa 5 gợi ý để dropdown gọn đẹp
        return list.size() > 5 ? list.subList(0, 5) : list;
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) List<String> brands,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        @RequestParam(required = false) String sort) {
        
        List<Product> results;
        if (categoryId != null) {
            // Tìm kiếm trong danh mục cụ thể
            results = productRepository.findByNameContainingIgnoreCase(q).stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("currentCategory", categoryRepository.findById(categoryId).get().getName());
            model.addAttribute("categoryId", categoryId);
        } else {
            // Tìm kiếm toàn hệ thống
            results = productRepository.findByNameContainingIgnoreCase(q);
        }

        applyFiltersAndSort(results, brands, minPrice, maxPrice, sort);

        model.addAttribute("products", results);
        model.addAttribute("searchQuery", q);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("currentBrands", brands);
        model.addAttribute("currentSort", sort);

        // Gợi ý sản phẩm thông minh
        if (!results.isEmpty()) {
            // Lấy danh mục của kết quả đầu tiên làm chuẩn gợi ý
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

    // Helper: Áp dụng lọc sản phẩm và sắp xếp (Yêu cầu C.1 & C.2)
    private void applyFiltersAndSort(List<Product> products, List<String> brands, Double minPrice, Double maxPrice, String sort) {
        // Lọc thương hiệu (Đa điều kiện)
        if (brands != null && !brands.isEmpty()) {
            products.removeIf(p -> p.getBrand() == null || !brands.contains(p.getBrand()));
        }
        
        // Lọc giá
        if (minPrice != null) {
            products.removeIf(p -> (p.getSalePrice() > 0 ? p.getSalePrice() : p.getOriginalPrice()) < minPrice);
        }
        if (maxPrice != null) {
            products.removeIf(p -> (p.getSalePrice() > 0 ? p.getSalePrice() : p.getOriginalPrice()) > maxPrice);
        }

        // Sắp xếp (Yêu cầu C.2)
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
        // Lấy sản phẩm cùng loại, loại bỏ chính nó
        List<Product> related = productRepository.findByCategoryName(product.getCategory().getName());
        related.removeIf(p -> p.getId().equals(id));
        model.addAttribute("relatedProducts", related);
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
                model.addAttribute("error", "Có lỗi xảy ra khi gửi email. Vui lòng thử lại sau.");
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
