package com.paper.factory.paper_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RFMAnalysis")
public class RFMAnalysis {

    @Id
    private String customerId;

    @Column(nullable = false)
    private Integer recency;

    @Column(nullable = false)
    private Integer frequency;

    @Column(nullable = false)
    private Double monetaryValue;

    @Column(nullable = false)
    private Double rfmValue; // 新增字段

    @Column(nullable = false)
    private String segment; // 新增字段

    @OneToOne
    @JoinColumn(name = "Customer_ID", referencedColumnName = "Customer_ID")
    private Customer customer;


    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getRecency() {
        return recency;
    }

    public void setRecency(Integer recency) {
        this.recency = recency;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Double getMonetaryValue() {
        return monetaryValue;
    }

    public void setMonetaryValue(Double monetaryValue) {
        this.monetaryValue = monetaryValue;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getRfmValue() {
        return rfmValue;
    }

    public void setRfmValue(Double rfmValue) {
        this.rfmValue = rfmValue;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }
}