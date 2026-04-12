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
                                  @RequestParam String paymentMethod) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        orderService.placeOrder(user, fullName, phoneNumber, address, paymentMethod);
        return "redirect:/order-history?success";
    }
}
