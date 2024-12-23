package com.paper.factory.paper_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @Column(name = "customer_id", unique = true)
    private Integer customer_id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String contact_info;

    @Column(length = 100)
    private String address;

    // Getters and Setters
    public Integer getCustomerId() {
        return customer_id;
    }

    public void setCustomerId(Integer customerId) {
        this.customer_id = customerId;
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
