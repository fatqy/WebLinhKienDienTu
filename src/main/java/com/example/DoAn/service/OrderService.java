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
import java.util.Optional;

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

    @Autowired
    private com.example.DoAn.repository.CouponRepository couponRepository;

    public Order placeOrder(User user, String fullName, String phoneNumber, String address, String paymentMethod, String couponCode) {
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

        double subtotal = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ hàng!");
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            double price = product.getSalePrice() > 0 ? product.getSalePrice() : product.getOriginalPrice();
            subtotal += price * cartItem.getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(price);
            orderItems.add(orderItem);
        }

        double total = subtotal;
        // Xử lý Coupon (Yêu cầu B.2)
        if (couponCode != null && !couponCode.isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCode(couponCode);
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (coupon.isActive() && (coupon.getExpiryDate() == null || !coupon.getExpiryDate().isBefore(java.time.LocalDate.now()))) {
                    if (subtotal >= coupon.getMinOrderValue()) {
                        double discount = 0;
                        if ("PERCENTAGE".equals(coupon.getDiscountType())) {
                            discount = subtotal * (coupon.getDiscountAmount() / 100);
                        } else {
                            discount = coupon.getDiscountAmount();
                        }
                        total = subtotal - discount;
                        order.setCoupon(coupon);
                    }
                }
            }
        }

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteByUser(user);
        
        return savedOrder;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public void cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        // Chỉ cho phép hủy khi đang ở trạng thái PENDING (Yêu cầu B.3)
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng khi đang ở trạng thái Chờ xác nhận");
        }

        // Hoàn lại số lượng vào kho (Yêu cầu B.1 nâng cao)
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
}
