package com.paper.factory.paper_system.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paper.factory.paper_system.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId")
    List<Order> findByCustomerId(@Param("customerId") String customerId);

 
    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId AND o.status = '已完成'")
    List<Order> findCompletedOrdersByCustomerId(@Param("customerId") String customerId);

    // 查找最近的訂單日期
    @Query("SELECT MAX(o.orderDate) FROM Order o WHERE o.customer.customerId = :customerId")
    Date findMostRecentOrderDate(@Param("customerId") String customerId);

}