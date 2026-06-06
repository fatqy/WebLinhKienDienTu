package com.example.DoAn.controller;

import com.example.DoAn.model.Product;
import com.example.DoAn.model.Review;
import com.example.DoAn.model.User;
import com.example.DoAn.model.Order;
import com.example.DoAn.repository.ProductRepository;
import com.example.DoAn.repository.ReviewRepository;
import com.example.DoAn.repository.OrderRepository;
import com.example.DoAn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserService userService;

    @PostMapping("/review/submit")
    public String submitReview(@RequestParam Long productId, 
                               @RequestParam int rating, 
                               @RequestParam String comment, 
                               RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        
        User user = userService.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Xác minh lại quyền đánh giá (đã mua & COMPLETED)
        List<Order> orders = orderRepository.findByUser(user);
        boolean canReview = orders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .flatMap(o -> o.getOrderItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(productId));
                
        if (!canReview) {
            redirectAttributes.addFlashAttribute("error", "Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua và nhận hàng thành công.");
            return "redirect:/product/" + productId;
        }

        Product product = productRepository.findById(productId).orElseThrow();
        
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setApproved(true); // Tự động duyệt
        
        reviewRepository.save(review);
        
        redirectAttributes.addFlashAttribute("success", "Đánh giá của bạn đã được gửi thành công!");
        return "redirect:/product/" + productId;
    }
}
