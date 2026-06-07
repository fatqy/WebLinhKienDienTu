package com.example.DoAn.repository;

import com.example.DoAn.model.CartItem;
import com.example.DoAn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProductId(User user, Long productId);
    void deleteByUser(User user);
    
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM CartItem c WHERE c.product.id = :productId")
    void deleteByProductId(@org.springframework.data.repository.query.Param("productId") Long productId);
}
