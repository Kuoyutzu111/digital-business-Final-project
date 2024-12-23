package com.paper.factory.paper_system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "BOM")
public class BOM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bomId;

    @ManyToOne
    @JoinColumn(name = "Product_ID", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @ManyToOne
    @JoinColumn(name = "Material_ID", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Material material;

    @Column(nullable = false)
    private Double requiredQuantity; // 每個產品的具體原料需求量（例如 960g 木漿）

     // **無參構造函數 (必須存在，JPA 需要)**
     public BOM() {
    }

    // **自定義構造函數，接受所有參數**
    public BOM(Integer bomId, Product product, Material material, Double requiredQuantity) {
        this.bomId = bomId;
        this.product = product;
        this.material = material;
        this.requiredQuantity = requiredQuantity;
    }

    // Getters and Setters
    public Integer getBomId() {
        return bomId;
    }

    public void setBomId(Integer bomId) {
        this.bomId = bomId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Double getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(Double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }
}
