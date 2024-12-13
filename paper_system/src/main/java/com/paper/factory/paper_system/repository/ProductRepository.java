package com.paper.factory.paper_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paper.factory.paper_system.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}

