package com.paper.factory.paper_system.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.paper.factory.paper_system.model.Customer;
import com.paper.factory.paper_system.model.Order;
import com.paper.factory.paper_system.model.OrderItem;
import com.paper.factory.paper_system.repository.CustomerRepository;
import com.paper.factory.paper_system.repository.OrderItemRepository;
import com.paper.factory.paper_system.repository.OrderRepository;
import com.paper.factory.paper_system.repository.ProductRepository;
import com.paper.factory.paper_system.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    // 新增訂單 API
    @PostMapping("/create")
    public Order createOrder(@RequestBody Map<String, Object> orderData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String employeeId = authentication.getName(); // 假设用户名是 Employee_ID

        return orderService.createOrder(orderData, employeeId);
    }

    // 獲取所有訂單，並查詢客戶名稱
    @GetMapping
    public List<Map<String, Object>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(order -> {
            Customer customer = customerRepository.findById(order.getCustomerId()).orElse(null);
            String customerName = (customer != null) ? customer.getName() : "N/A";

            // 使用 HashMap 代替 Map.of
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderId", order.getOrderId());
            orderData.put("orderDate", order.getOrderDate());
            orderData.put("customerName", customerName);
            orderData.put("deliveryDate", order.getDeliveryDate());
            orderData.put("status", order.getStatus());

            return orderData;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{orderId}/details")
    @ResponseBody
    public Map<String, Object> getOrderDetails(@PathVariable Integer orderId) {
        // 獲取訂單
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("訂單不存在，ID: " + orderId);
        }

        // 獲取訂單項目
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);

        // 格式化結果
        List<Map<String, Object>> items = orderItems.stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", item.getProductId());
            String productName = productRepository.findById(item.getProductId())
                    .map(product -> product.getName()) // 如果找到產品，返回名稱
                    .orElse("未知產品"); // 如果未找到，返回 "未知產品"

            itemMap.put("productName", productName);
            itemMap.put("quantity", item.getQuantity());
            return itemMap;
        }).collect(Collectors.toList());

        // 返回詳情
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.getOrderId());
        response.put("orderDate", order.getOrderDate());
        response.put("deliveryDate", order.getDeliveryDate());
        response.put("status", order.getStatus());
        response.put("items", items);
        response.put("modifiable", "未開始".equals(order.getStatus())); // 狀態是否允許修改

        return response;
    }

    @PutMapping("/{orderId}/items/{productId}/update")
    public ResponseEntity<String> updateOrderItemQuantity(
            @PathVariable Integer orderId,
            @PathVariable Integer productId,
            @RequestBody Map<String, Integer> request) {

        Integer quantity = request.get("quantity");
        if (quantity == null || quantity < 1) {
            return ResponseEntity.badRequest().body("數量必須大於 0");
        }

        // 查找訂單項目
        OrderItem orderItem = orderItemRepository.findByOrderOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的訂單項目"));

        // 更新數量
        orderItem.setQuantity(quantity);
        orderItemRepository.save(orderItem);

        return ResponseEntity.ok("數量更新成功");
    }

    @GetMapping("/customer-analysis")
    public List<Map<String, Object>> getCustomerAnalysis() {
        return orderService.analyzeCustomers();
    }

}