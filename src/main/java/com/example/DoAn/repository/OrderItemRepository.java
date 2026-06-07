package com.example.DoAn.repository;

import com.example.DoAn.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM OrderItem o WHERE o.product.id = :productId")
    void deleteByProductId(@org.springframework.data.repository.query.Param("productId") Long productId);
}
