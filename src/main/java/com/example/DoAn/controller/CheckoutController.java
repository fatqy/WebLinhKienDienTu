package com.example.DoAn.controller;

import com.example.DoAn.model.User;
import com.example.DoAn.service.CartService;
import com.example.DoAn.service.OrderService;
import com.example.DoAn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String showCheckout(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        if (cartService.getCartItems(user).isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cartItems", cartService.getCartItems(user));
        model.addAttribute("total", cartService.calculateTotal(user));
        model.addAttribute("user", user);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(Authentication authentication,
                                  @RequestParam String fullName,
                                  @RequestParam String phoneNumber,
                                  @RequestParam String address,
                                  @RequestParam String paymentMethod,
                                  @RequestParam(required = false) String couponCode) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        orderService.placeOrder(user, fullName, phoneNumber, address, paymentMethod, couponCode);
        return "redirect:/order-history?success";
    }

    @Autowired
    private com.example.DoAn.repository.CouponRepository couponRepository;

    @GetMapping("/api/coupon/validate")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.Map<String, Object> validateCoupon(@RequestParam String code, @RequestParam double subtotal) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        java.util.Optional<com.example.DoAn.model.Coupon> couponOpt = couponRepository.findByCode(code);
        
        if (couponOpt.isPresent()) {
            com.example.DoAn.model.Coupon coupon = couponOpt.get();
            if (!coupon.isActive()) {
                response.put("valid", false);
                response.put("message", "Mã giảm giá đã bị vô hiệu hóa.");
            } else if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(java.time.LocalDate.now())) {
                response.put("valid", false);
                response.put("message", "Mã giảm giá đã hết hạn.");
            } else if (subtotal < coupon.getMinOrderValue()) {
                response.put("valid", false);
                response.put("message", "Đơn hàng tối thiểu " + String.format("%,.0f₫", coupon.getMinOrderValue()) + " để dùng mã này.");
            } else {
                double discount = 0;
                if ("PERCENTAGE".equals(coupon.getDiscountType())) {
                    discount = subtotal * (coupon.getDiscountAmount() / 100);
                } else {
                    discount = coupon.getDiscountAmount();
                }
                response.put("valid", true);
                response.put("discount", discount);
                response.put("message", "Áp dụng thành công! Giảm " + String.format("%,.0f₫", discount));
            }
        } else {
            response.put("valid", false);
            response.put("message", "Mã giảm giá không tồn tại.");
        }
        return response;
    }
}
