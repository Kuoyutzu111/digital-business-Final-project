package com.paper.factory.paper_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paper.factory.paper_system.model.OrderMaterialRequirement;

@Repository
public interface OrderMaterialRequirementRepository extends JpaRepository<OrderMaterialRequirement, Integer> {

    @Query("SELECT omr FROM OrderMaterialRequirement omr " +
           "WHERE omr.order.orderId = :orderId AND omr.material.materialId = :materialId")
    Optional<OrderMaterialRequirement> findByOrderOrderIdAndMaterialId(@Param("orderId") Integer orderId,
                                                                       @Param("materialId") Integer materialId);

    @Query("SELECT COALESCE(SUM(omr.totalRequiredQuantity), 0) " +
           "FROM OrderMaterialRequirement omr WHERE omr.material.materialId = :materialId")
    Double findTotalRequiredByMaterialId(@Param("materialId") Integer materialId);

     @Modifying
    @Query("DELETE FROM OrderMaterialRequirement omr WHERE omr.order.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Integer orderId);

     List<OrderMaterialRequirement> findByOrderOrderId(Integer orderId);
}


