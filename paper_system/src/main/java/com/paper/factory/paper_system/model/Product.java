package com.paper.factory.paper_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(nullable = false, length = 20)
    private String unit; // 例如 "24包", "12卷"

    // **無參構造函數 (必須存在，JPA 需要)**
    public Product() {
    }

    // **自定義構造函數，接受所有參數**
    public Product(Integer productId, String name, Double unitPrice, String unit) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.unit = unit;
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
