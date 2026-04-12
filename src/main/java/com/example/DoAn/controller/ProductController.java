package com.example.DoAn.controller;

import com.example.DoAn.model.Category;
import com.example.DoAn.model.FilterGroup;
import com.example.DoAn.model.Product;
import com.example.DoAn.repository.CategoryRepository;
import com.example.DoAn.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Tự động thêm categories vào mọi trang để không bao giờ lỗi Navbar
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
    public String getCollectionPage(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "linh-kien-may-tinh";
    }

    @GetMapping("/category/{id}")
    public String getByCategory(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).orElseThrow();
        model.addAttribute("products", category.getProducts());
        model.addAttribute("currentCategory", category.getName());
        return "linh-kien-may-tinh";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        model.addAttribute("products", productRepository.findByNameContainingIgnoreCase(q));
        model.addAttribute("searchQuery", q);
        return "linh-kien-may-tinh";
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
}
