package com.example.DoAn.repository;

import com.example.DoAn.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryName(String categoryName);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Lấy sản phẩm có giá giảm (salePrice > 0)
    List<Product> findBySalePriceGreaterThan(double price);

    // Lấy 8 sản phẩm mới nhất
    List<Product> findTop8ByOrderByIdDesc();

    // Pessimistic Lock để chống Race Condition khi đặt hàng
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    // Tìm kiếm và lọc nâng cao tại DB (Tối ưu hiệu năng)
    @Query("SELECT p FROM Product p WHERE " +
           "(:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "((p.salePrice > 0 AND p.salePrice >= :minPrice AND p.salePrice <= :maxPrice) OR " +
           " (p.salePrice = 0 AND p.originalPrice >= :minPrice AND p.originalPrice <= :maxPrice))")
    List<Product> findByFilters(@Param("q") String q,
                               @Param("categoryId") Long categoryId,
                               @Param("minPrice") double minPrice,
                               @Param("maxPrice") double maxPrice);
}
