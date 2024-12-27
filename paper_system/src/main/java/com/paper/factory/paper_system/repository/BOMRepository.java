package com.paper.factory.paper_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paper.factory.paper_system.model.BOM;

@Repository
public interface BOMRepository extends JpaRepository<BOM, Integer> {
    List<BOM> findByProductProductId(Integer productId);
}
