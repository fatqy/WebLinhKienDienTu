package com.example.DoAn.repository;

import com.example.DoAn.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryName(String categoryName);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Lấy sản phẩm có giá giảm (salePrice > 0)
    List<Product> findBySalePriceGreaterThan(double price);

    // Lấy 8 sản phẩm mới nhất
    List<Product> findTop8ByOrderByIdDesc();
}
