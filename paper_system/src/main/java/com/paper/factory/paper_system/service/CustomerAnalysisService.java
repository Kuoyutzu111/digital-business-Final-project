package com.paper.factory.paper_system.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.Customer;
import com.paper.factory.paper_system.model.Order;
import com.paper.factory.paper_system.repository.CustomerRepository;
import com.paper.factory.paper_system.repository.OrderRepository;

@Service
public class CustomerAnalysisService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Map<String, Object>> calculateCustomerAnalysis() {
        List<Customer> customers = customerRepository.findAll(); // 獲取所有顧客
        List<Map<String, Object>> analysisResults = new ArrayList<>();

        for (Customer customer : customers) {
            List<Order> orders = orderRepository.findByCustomerId(customer.getCustomerId()); // 獲取顧客的所有訂單

            // 計算總消費金額
            double totalSpent = orders.stream()
                    .mapToDouble(order -> calculateOrderTotal(order))
                    .sum();

            // 計算平均客單價
            double averageOrderValue = orders.isEmpty() ? 0 : totalSpent / orders.size();

            // 計算活躍機率 (假設最近 30 天內有下單為活躍)
            double activityProbability = calculateActivityProbability(orders);

            // 計算顧客終身價值 (CLV)
            double customerLifetimeValue = averageOrderValue * orders.size();

            // 構建結果
            Map<String, Object> customerAnalysis = new HashMap<>();
            customerAnalysis.put("customerId", customer.getCustomerId());
            customerAnalysis.put("activityProbability", activityProbability);
            customerAnalysis.put("averageOrderValue", averageOrderValue);
            customerAnalysis.put("customerLifetimeValue", customerLifetimeValue);

            analysisResults.add(customerAnalysis);
        }

        return analysisResults;
    }

    private double calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .mapToDouble(item -> {
                    // 查找產品的價格
                    double productPrice = item.getProductId() * 1.0; // 假設每個商品有價格字段
                    return productPrice * item.getQuantity();
                })
                .sum();
    }

    private double calculateActivityProbability(List<Order> orders) {
        long activeOrders = orders.stream()
                .filter(order -> order.getOrderDate()
                        .after(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)))
                .count();
        return orders.isEmpty() ? 0 : (double) activeOrders / orders.size() * 100;
    }
}
