package com.example.DoAn.controller;

import com.example.DoAn.model.User;
import com.example.DoAn.service.CartService;
import com.example.DoAn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("cartItems", cartService.getCartItems(user));
        model.addAttribute("total", cartService.calculateTotal(user));
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity, Authentication authentication) {
        if(authentication == null) return "redirect:/login";
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        cartService.addToCart(user, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/api/add")
    @ResponseBody
    public String addToCartApi(@RequestParam Long productId, @RequestParam int quantity, Authentication authentication) {
        if (authentication == null) {
            return "unauthorized";
        }
        try {
            User user = userService.findByUsername(authentication.getName()).orElseThrow();
            cartService.addToCart(user, productId, quantity);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        cartService.removeFromCart(id, user);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateCart(@RequestParam Long id, @RequestParam int quantity, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName()).orElseThrow();
            cartService.updateQuantity(id, quantity, user);
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
