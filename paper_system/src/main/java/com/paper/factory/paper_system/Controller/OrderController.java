package com.paper.factory.paper_system.Controller;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paper.factory.paper_system.model.Order;
import com.paper.factory.paper_system.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 新增訂單 API
    @PostMapping("/create")
    public Order createOrder(@RequestBody Map<String, Object> orderData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String employeeId = authentication.getName(); // 假设用户名是 Employee_ID

        return orderService.createOrder(orderData, employeeId);
    }
}
