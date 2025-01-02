package com.paper.factory.paper_system.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.Order;
import com.paper.factory.paper_system.model.Product;
import com.paper.factory.paper_system.model.RFMAnalysis;
import com.paper.factory.paper_system.repository.OrderRepository;
import com.paper.factory.paper_system.repository.ProductRepository;
import com.paper.factory.paper_system.repository.RFMAnalysisRepository;

import jakarta.transaction.Transactional;

@Service
public class RFMService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RFMAnalysisRepository rfmAnalysisRepository;

    @Autowired
    private ProductRepository productRepository; // 新增

    @Transactional
    public void calculateAndSaveRFM() {
        // Fetch all orders
        List<Order> orders = orderRepository.findAll();
    
        // Group orders by Customer ID
        Map<String, List<Order>> ordersByCustomer = orders.stream()
                .filter(order -> order.getCustomerId() != null)
                .collect(Collectors.groupingBy(Order::getCustomerId));
    
        for (Map.Entry<String, List<Order>> entry : ordersByCustomer.entrySet()) {
            String customerId = entry.getKey();
            List<Order> customerOrders = entry.getValue();
    
            // Calculate Recency
            LocalDate lastOrderDate = customerOrders.stream()
                .map(order -> ((java.sql.Date) order.getOrderDate()).toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
        LocalDate today = LocalDate.now();

        long recency = ChronoUnit.DAYS.between(lastOrderDate, today);
        if (recency < 0) {
            recency = 0; // 防止負值
        }
            // Calculate Frequency
            long frequency = customerOrders.size();
    
            // Calculate Monetary
            double monetary = customerOrders.stream()
                    .flatMap(order -> order.getOrderItems().stream())
                    .mapToDouble(item -> {
                        Product product = productRepository.findById(item.getProductId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Product not found for ID: " + item.getProductId()));
                        return product.getUnitPrice() * item.getQuantity();
                    })
                    .sum();
    
            // Assign Segment
            String segment = assignSegment(recency, frequency, monetary);
            double rfmValue = calculateRFMValue(recency, frequency, monetary);
    
            // Save to RFMAnalysis
            RFMAnalysis rfmAnalysis = rfmAnalysisRepository.findById(customerId)
                    .orElse(new RFMAnalysis());
            rfmAnalysis.setCustomerId(customerId);
            rfmAnalysis.setRecency((int) recency);
            rfmAnalysis.setFrequency((int) frequency);
            rfmAnalysis.setMonetaryValue(monetary);
            rfmAnalysis.setSegment(segment);
            rfmAnalysis.setRfmValue(rfmValue); 
    
            rfmAnalysisRepository.save(rfmAnalysis);
        }
    }
    private double calculateRFMValue(long recency, long frequency, double monetary) {
        return (1000 - recency) * 0.5 + frequency * 1.5 + monetary * 2.0;
    }
    
    

    private String assignSegment(long recency, long frequency, double monetary) {
        // 簡單的分段邏輯，可根據需求調整
        if (recency <= 30 && frequency > 5 && monetary > 500) {
            return "高價值客戶";
        } else if (frequency > 2 && monetary > 200) {
            return "潛力客戶";
        } else {
            return "一般客戶";
        }
    }





    public List<RFMAnalysis> getRFMResults() {
        return rfmAnalysisRepository.findAll();
    }
}
