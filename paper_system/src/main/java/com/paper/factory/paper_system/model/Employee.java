package com.paper.factory.paper_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String department;

    @Column(length = 20)
    private String role;

    // Getters and Setters
    public Integer getEId() {
        return eId;
    }

    public void setEId(Integer eId) {
        this.eId = eId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
