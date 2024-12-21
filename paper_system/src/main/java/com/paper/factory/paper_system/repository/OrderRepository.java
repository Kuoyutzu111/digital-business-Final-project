package com.paper.factory.paper_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paper.factory.paper_system.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o.customerId, MAX(o.orderDate), COUNT(o), SUM(oi.quantity * p.unitPrice) " +
           "FROM Order o " +
           "JOIN OrderItem oi ON o.orderId = oi.orderId " +
           "JOIN Product p ON oi.productId = p.productId " +
           "GROUP BY o.customerId")
    List<Object[]> calculateRFM();
}
