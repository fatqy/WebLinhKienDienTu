package com.example.DoAn.service;

import com.example.DoAn.model.CartItem;
import com.example.DoAn.model.Product;
import com.example.DoAn.model.User;
import com.example.DoAn.repository.CartItemRepository;
import com.example.DoAn.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public void addToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Số lượng đặt mua phải lớn hơn 0");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Số lượng trong kho không đủ");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProductId(user, productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Tổng số lượng vượt quá hàng tồn kho");
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    public double calculateTotal(User user) {
        return getCartItems(user).stream()
                .mapToDouble(item -> {
                    double price = item.getProduct().getOriginalPrice();
                    if (item.getProduct().getSalePrice() > 0 && item.getProduct().getSalePrice() < price) {
                        price = item.getProduct().getSalePrice();
                    }
                    return price * item.getQuantity();
                })
                .sum();
    }

    public void removeFromCart(Long cartItemId, User user) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Mặt hàng không tồn tại trong giỏ hàng"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa mặt hàng này");
        }
        cartItemRepository.delete(item);
    }
    
    public void updateQuantity(Long cartItemId, int quantity, User user) {
        if (quantity <= 0) {
            removeFromCart(cartItemId, user);
            return;
        }
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Mặt hàng không tồn tại trong giỏ hàng"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật mặt hàng này");
        }
        if (item.getProduct().getStock() < quantity) {
            throw new RuntimeException("Không đủ hàng trong kho");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }
}
