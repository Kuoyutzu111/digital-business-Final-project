package com.paper.factory.paper_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "OrderMaterialRequirement")
public class OrderMaterialRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requirementId;

    @ManyToOne
    @JoinColumn(name = "Order_ID", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(nullable = false)
    private Double totalRequiredQuantity;

    // Getters and Setters
    public Integer getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Integer requirementId) {
        this.requirementId = requirementId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Material getMaterialId() {
        return material;
    }

    public void setMaterialId(Material materialId) {
        this.material = materialId;
    }


    public Double getTotalRequiredQuantity() {
        return totalRequiredQuantity;
    }

    public void setTotalRequiredQuantity(Double totalRequiredQuantity) {
        this.totalRequiredQuantity = totalRequiredQuantity;
    }
}