package com.example.DoAn.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private double originalPrice;
    private double salePrice;
    
    private int stock; // Số lượng tồn kho
    private String imageUrl;
    @Column(length = 2000)
    private String extraImages; // Danh sách URL ảnh phụ, cách nhau bởi dấu phẩy
    private String badge; // Ví dụ: Hot Deal, New
    private String status; // Ví dụ: Còn hàng, Hết hàng
    private String brand; // Thương hiệu

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public Product() {}

    public Product(Long id, String name, double originalPrice, double salePrice, int stock, String imageUrl, String badge, String status) {
        this.id = id;
        this.name = name;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.badge = badge;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getExtraImages() { return extraImages; }
    public void setExtraImages(String extraImages) { this.extraImages = extraImages; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public String getFormattedSalePrice() {
        return String.format("%,.0f₫", salePrice > 0 ? salePrice : originalPrice);
    }

    public String getFormattedCurrentPrice() {
        return getFormattedSalePrice();
    }

    public String getFormattedOriginalPrice() {
        return String.format("%,.0f₫", originalPrice);
    }
    
    public int getDiscountPercentage() {
        if (originalPrice <= salePrice || salePrice <= 0) return 0;
        return (int) ((originalPrice - salePrice) / originalPrice * 100);
    }
}
