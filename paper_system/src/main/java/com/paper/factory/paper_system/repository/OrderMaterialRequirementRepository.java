package com.paper.factory.paper_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paper.factory.paper_system.model.OrderMaterialRequirement;

@Repository
public interface OrderMaterialRequirementRepository extends JpaRepository<OrderMaterialRequirement, Integer> {
    Optional<OrderMaterialRequirement> findByOrderOrderIdAndMaterialId(Integer orderId, Integer materialId);
}


