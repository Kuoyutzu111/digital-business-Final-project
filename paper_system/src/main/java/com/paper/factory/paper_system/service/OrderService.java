package com.paper.factory.paper_system.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.BOM;
import com.paper.factory.paper_system.model.Customer;
import com.paper.factory.paper_system.model.Order;
import com.paper.factory.paper_system.model.OrderItem;
import com.paper.factory.paper_system.model.OrderMaterialRequirement;
import com.paper.factory.paper_system.model.Product;
import com.paper.factory.paper_system.repository.BOMRepository;
import com.paper.factory.paper_system.repository.CustomerRepository;
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

    @Transactional
    public Order createOrder(Map<String, Object> orderData, String employeeId) {
        // 创建订单
        Order order = new Order();
        order.setOrderDate(java.sql.Date.valueOf((String) orderData.get("orderDate")));
        order.setDeliveryDate(java.sql.Date.valueOf((String) orderData.get("deliveryDate")));
        order.setStatus("未开始");

        // 設置客戶
        Integer customerId = Integer.parseInt((String) orderData.get("customerId"));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("客戶不存在，ID: " + customerId));
        order.setCustomer(customer);

        // 保存订单
        Order savedOrder = orderRepository.save(order);
        if (savedOrder.getOrderId() == null) {
            throw new IllegalStateException("Order 保存失败，未生成主键 ID");
        }
        System.out.println("保存的 Order: " + savedOrder);

        // 处理订单项
        List<Map<String, Object>> products = (List<Map<String, Object>>) orderData.get("products");
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map<String, Object> productData : products) {
            Integer productId = Integer.parseInt((String) productData.get("productId"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("產品不存在，ID: " + productId));

            Integer quantity = Integer.parseInt((String) productData.get("quantity"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);

            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);

        // 打印调试信息
        for (OrderItem item : orderItems) {
            System.out.println("保存的 OrderItem: " + item);
            System.out.println("OrderItem 的 Order_ID: " + item.getOrder().getOrderId());
        }

        return savedOrder;
    }

    private void calculateAndSaveMaterialRequirements(Order order, List<OrderItem> orderItems) {
        List<OrderMaterialRequirement> materialRequirements = new ArrayList<>();
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            Integer quantity = item.getQuantity();

            // 获取 BOM 列表
            List<BOM> boms = bomRepository.findByProduct(product);

            for (BOM bom : boms) {
                Integer materialId = bom.getMaterial().getMaterialId();
                Double requiredQuantity = bom.getRequiredQuantity();
                Double totalRequiredQuantity = requiredQuantity * quantity;

                // 检查是否已经存在
                Optional<OrderMaterialRequirement> existingRequirement = orderMaterialRequirementRepository
                        .findByOrderOrderIdAndMaterialId(order.getOrderId(), materialId);

                if (existingRequirement.isPresent()) {
                    OrderMaterialRequirement requirement = existingRequirement.get();
                    requirement
                            .setTotalRequiredQuantity(requirement.getTotalRequiredQuantity() + totalRequiredQuantity);
                    orderMaterialRequirementRepository.save(requirement);
                } else {
                    OrderMaterialRequirement requirement = new OrderMaterialRequirement();
                    requirement.setOrder(order);
                    requirement.setMaterialId(materialId);
                    requirement.setTotalRequiredQuantity(totalRequiredQuantity);
                    materialRequirements.add(requirement);
                }
            }
        }

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
        // 假設每個 Order 包含 OrderItems，計算訂單總金額
        return order.getOrderItems().stream()
                .mapToDouble(item -> item.getProduct().getUnitPrice() * item.getQuantity())
                .sum();
    }

    private double calculateActivityProbability(List<Order> orders) {
        // 假設活動定義為最近 30 天內下過訂單
        long activeOrders = orders.stream()
                .filter(order -> order.getOrderDate().after(Date.from(Instant.now().minus(30, ChronoUnit.DAYS))))
                .count();

        return orders.isEmpty() ? 0 : (double) activeOrders / orders.size() * 100;
    }
}