package com.paper.factory.paper_system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.BOM;
import com.paper.factory.paper_system.model.Order;
import com.paper.factory.paper_system.model.OrderItem;
import com.paper.factory.paper_system.model.OrderMaterialRequirement;
import com.paper.factory.paper_system.repository.BOMRepository;
import com.paper.factory.paper_system.repository.CustomerRepository;
import com.paper.factory.paper_system.repository.OrderItemRepository;
import com.paper.factory.paper_system.repository.OrderMaterialRequirementRepository;
import com.paper.factory.paper_system.repository.OrderRepository;

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

    @Transactional
    public Order createOrder(Map<String, Object> orderData, String employeeId) {
        // 创建订单
        Order order = new Order();
        order.setOrderDate(java.sql.Date.valueOf((String) orderData.get("orderDate")));
        order.setDeliveryDate(java.sql.Date.valueOf((String) orderData.get("deliveryDate")));
        order.setStatus("未开始");
        order.setCustomerId(Integer.parseInt((String) orderData.get("customerId")));
        order.setEmployeeId(employeeId);

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
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder); // 设置正确的 Order
            orderItem.setProductId(Integer.parseInt((String) productData.get("productId")));
            orderItem.setQuantity(Integer.parseInt((String) productData.get("quantity")));
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
            Integer productId = item.getProductId();
            Integer quantity = item.getQuantity();

            // 获取 BOM 列表
            List<BOM> boms = bomRepository.findByProductProductId(productId);

            for (BOM bom : boms) {
                Integer materialId = bom.getMaterial().getMaterialId();
                Double requiredQuantity = bom.getRequiredQuantity();
                Double totalRequiredQuantity = requiredQuantity * quantity;

                // 检查是否已经存在
                Optional<OrderMaterialRequirement> existingRequirement = orderMaterialRequirementRepository
                        .findByOrderOrderIdAndMaterialId(order.getOrderId(), materialId);

                if (existingRequirement.isPresent()) {
                    OrderMaterialRequirement requirement = existingRequirement.get();
                    requirement.setTotalRequiredQuantity(requirement.getTotalRequiredQuantity() + totalRequiredQuantity);
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
}