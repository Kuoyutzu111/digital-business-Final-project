package com.paper.factory.paper_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Material")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer materialId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "Stock_Quantity", nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Integer reorderThreshold;

    @Column(nullable = false)
    private Integer reorderQuantity;

    // **無參構造函數 (必須存在，JPA 需要)**
    public Material() {
    }

    // **自定義構造函數，接受所有參數**
    public Material(Integer materialId, String name, Integer stockQuantity, Integer reorderThreshold, Integer reorderQuantity) {
        this.materialId = materialId;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.reorderThreshold = reorderThreshold;
        this.reorderQuantity = reorderQuantity;
    }

    // Getters and Setters
    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(Integer reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public Integer getReorderQuantity() {
        return reorderQuantity;
    }

    public void setReorderQuantity(Integer reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }
}
