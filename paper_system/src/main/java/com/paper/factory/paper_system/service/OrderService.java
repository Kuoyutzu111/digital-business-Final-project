package com.paper.factory.paper_system.service;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.BOM;
import com.paper.factory.paper_system.model.Customer;
import com.paper.factory.paper_system.model.Material;
import com.paper.factory.paper_system.model.Order;
import com.paper.factory.paper_system.model.OrderItem;
import com.paper.factory.paper_system.model.OrderMaterialRequirement;
import com.paper.factory.paper_system.model.Product;
import com.paper.factory.paper_system.repository.BOMRepository;
import com.paper.factory.paper_system.repository.CustomerRepository;
import com.paper.factory.paper_system.repository.EmployeeRepository;
import com.paper.factory.paper_system.repository.MaterialRepository;
import com.paper.factory.paper_system.repository.OrderItemRepository;
import com.paper.factory.paper_system.repository.OrderMaterialRequirementRepository;
import com.paper.factory.paper_system.repository.OrderRepository;
import com.paper.factory.paper_system.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderMaterialRequirementRepository orderMaterialRequirementRepository;

    @Autowired
    private BOMRepository bomRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MaterialRepository materialRepository;

    public void updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }
    
    @Transactional
public Order createOrder(Map<String, Object> orderData, String employeeId) {
    // 保持原有的訂單創建邏輯
    Order order = new Order();
    order.setOrderDate(java.sql.Date.valueOf((String) orderData.get("orderDate")));
    order.setDeliveryDate(java.sql.Date.valueOf((String) orderData.get("deliveryDate")));
    order.setStatus("未開始");
    order.setCustomerId((String) orderData.get("customerId"));
    order.setEmployeeId(employeeId);

    Order savedOrder = orderRepository.save(order);
    List<Map<String, Object>> products = (List<Map<String, Object>>) orderData.get("products");
    List<OrderItem> orderItems = new ArrayList<>();
    for (Map<String, Object> productData : products) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setProductId(Integer.parseInt((String) productData.get("productId")));
        orderItem.setQuantity(Integer.parseInt((String) productData.get("quantity")));
        orderItems.add(orderItem);
    }
    orderItemRepository.saveAll(orderItems);

    // 計算原材料需求並扣減庫存
    calculateAndSaveMaterialRequirements(savedOrder, orderItems);
    updateMaterialStock(savedOrder);

    return savedOrder;
}

// 更新原材料庫存
private void updateMaterialStock(Order order) {
    List<OrderMaterialRequirement> requirements = orderMaterialRequirementRepository.findByOrderOrderId(order.getOrderId());
    for (OrderMaterialRequirement requirement : requirements) {
        Material material = requirement.getMaterialId();
        int newStock = material.getStockQuantity() - requirement.getTotalRequiredQuantity().intValue();
        material.setStockQuantity(newStock);
        materialRepository.save(material);
    }
}


    private void calculateAndSaveMaterialRequirements(Order order, List<OrderItem> orderItems) {
    if (order.getOrderId() == null) {
        throw new IllegalArgumentException("Order 尚未保存，无法计算原材料需求！");
    }

    List<OrderMaterialRequirement> materialRequirements = new ArrayList<>();
    for (OrderItem item : orderItems) {
        Integer productId = item.getProductId();
        Integer quantity = item.getQuantity();

        // 获取 BOM 列表
        List<BOM> boms = bomRepository.findByProductProductId(productId);

        for (BOM bom : boms) {
            Material material = bom.getMaterial(); // 確保這是 Material 實體
            Double requiredQuantity = bom.getRequiredQuantity();
            Double totalRequiredQuantity = requiredQuantity * quantity;

            // 检查是否已经存在
            Optional<OrderMaterialRequirement> existingRequirement = orderMaterialRequirementRepository
                    .findByOrderOrderIdAndMaterialId(order.getOrderId(), material.getMaterialId());

            if (existingRequirement.isPresent()) {
                OrderMaterialRequirement requirement = existingRequirement.get();
                requirement.setTotalRequiredQuantity(
                    requirement.getTotalRequiredQuantity() + totalRequiredQuantity
                );
                orderMaterialRequirementRepository.save(requirement);
            } else {
                OrderMaterialRequirement requirement = new OrderMaterialRequirement();
                requirement.setOrder(order); // 傳入持久化的 Order
                requirement.setMaterialId(material); // 傳入 Material 實體
                requirement.setTotalRequiredQuantity(totalRequiredQuantity);
                materialRequirements.add(requirement);
            }
        }
    }

    // 保存新創建的 MaterialRequirement
    orderMaterialRequirementRepository.saveAll(materialRequirements);
}


    public List<Map<String, Object>> analyzeCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<Map<String, Object>> analysisResults = new ArrayList<>();
        for (Customer customer : customers) {
            // 查詢顧客的所有訂單
            List<Order> orders = orderRepository.findByCustomerId(customer.getCustomerId());
            // 計算總消費金額
            double totalSpent = orders.stream()
                    .mapToDouble(order -> calculateOrderTotal(order))
                    .sum();
            // 計算平均客單價
            double averageOrderValue = orders.isEmpty() ? 0 : totalSpent / orders.size();
            // 計算活躍機率
            double activityProbability = calculateActivityProbability(orders);
            // 計算顧客終身價值
            double customerLifetimeValue = averageOrderValue * orders.size();
            // 將數據放入 Map
            Map<String, Object> customerAnalysis = new HashMap<>();
            customerAnalysis.put("customerId", customer.getCustomerId());
            customerAnalysis.put("customerName", customer.getName());
            customerAnalysis.put("activityProbability", activityProbability);
            customerAnalysis.put("averageOrderValue", averageOrderValue);
            customerAnalysis.put("customerLifetimeValue", customerLifetimeValue);
            // 添加到結果列表
            analysisResults.add(customerAnalysis);
        }
        return analysisResults;
    }

    private double calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .mapToDouble(item -> {
                    // 根據 productId 查詢 Product
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Product not found with ID: " + item.getProductId()));

                    // 確保 unitPrice 不為空，否則拋出異常
                    if (product.getUnitPrice() == null) {
                        throw new IllegalArgumentException(
                                "Product unit price is null for product ID: " + item.getProductId());
                    }

                    // 計算該商品的總價
                    return product.getUnitPrice() * item.getQuantity();
                })
                .sum(); // 將所有商品的總價加總
    }

    private double calculateActivityProbability(List<Order> orders) {
        // 假設活動定義為最近 30 天內下過訂單
        long activeOrders = orders.stream()
                .filter(order -> order.getOrderDate().after(Date.from(Instant.now().minus(30, ChronoUnit.DAYS))))
                .count();
        return orders.isEmpty() ? 0 : (double) activeOrders / orders.size() * 100;
    }

}