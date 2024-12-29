package com.paper.factory.paper_system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Customer")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Customer {

    @Id
    @Column(name = "Customer_ID", columnDefinition = "VARCHAR(20)")
    private String customerId; // 修改為 String 型別

    @Column(nullable = false, length = 50)
    private String name;

    @JsonProperty("contactInfo")
    @Column(nullable = false, length = 100)
    private String contact_info;

    @Column(length = 100)
    private String address;

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contact_info;
    }

    public void setContactInfo(String contactInfo) {
        this.contact_info = contactInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}