package com.example.DoAn.controller;

import com.example.DoAn.model.User;
import com.example.DoAn.service.OrderService;
import com.example.DoAn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/order-history")
    public String viewOrderHistory(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("orders", orderService.getUserOrders(user));
        return "order-history";
    }

    @PostMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(authentication.getName()).orElseThrow();
            orderService.cancelOrder(id, user);
            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng thành công và hoàn lại kho.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/order-history";
    }
}
