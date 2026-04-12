package com.example.DoAn.service;

import com.example.DoAn.model.*;
import com.example.DoAn.repository.CartItemRepository;
import com.example.DoAn.repository.OrderItemRepository;
import com.example.DoAn.repository.OrderRepository;
import com.example.DoAn.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order placeOrder(User user, String fullName, String phoneNumber, String address, String paymentMethod) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        Order order = new Order();
        order.setUser(user);
        order.setFullName(fullName);
        order.setPhoneNumber(phoneNumber);
        order.setShippingAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING"); // Chờ xác nhận

        double total = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            // Kiểm tra tồn kho lần cuối
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ hàng!");
            }

            // Trừ kho (Yêu cầu B.1)
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            double price = product.getSalePrice() > 0 ? product.getSalePrice() : product.getOriginalPrice();
            total += price * cartItem.getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(price);
            orderItems.add(orderItem);
        }

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);
        
        // Xóa giỏ hàng sau khi đặt thành công
        cartItemRepository.deleteByUser(user);
        
        return savedOrder;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }
}
