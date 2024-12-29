package com.paper.factory.paper_system.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.Customer;
import com.paper.factory.paper_system.model.Order;
import com.paper.factory.paper_system.model.RFMAnalysis;
import com.paper.factory.paper_system.repository.OrderRepository;
import com.paper.factory.paper_system.repository.RFMAnalysisRepository;

@Service
public class RFMService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RFMAnalysisRepository rfmAnalysisRepository;

    public List<RFMAnalysis> getRFMResults() {
        return rfmAnalysisRepository.findAll();
    }

    public void calculateAndSaveRFM() {
        List<Customer> customers = orderRepository.findAll()
                .stream()
                .map(Order::getCustomer)
                .distinct()
                .toList();

        List<RFMAnalysis> rfmAnalysisList = new ArrayList<>();

        for (Customer customer : customers) {
            List<Order> orders = orderRepository.findCompletedOrdersByCustomerId(customer.getCustomerId());

            if (!orders.isEmpty()) {
                // Recency
                LocalDate mostRecentDate = orders.stream()
                        .map(order -> order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.now());
                long recency = ChronoUnit.DAYS.between(mostRecentDate, LocalDate.now());

                // Frequency
                int frequency = orders.size();

                // Monetary
                double monetary = orders.stream()
                        .flatMap(order -> order.getOrderItems().stream())
                        .mapToDouble(item -> item.getQuantity() * item.getPrice())
                        .sum();

                // RFM Value
                double rfmValue = (1.0 / (recency + 1)) * 0.4 + frequency * 0.3 + monetary * 0.3;

                // 分類
                String segment = classifySegment(rfmValue);

                // 保存到 RFMAnalysis 实体
                RFMAnalysis rfmAnalysis = new RFMAnalysis();
                rfmAnalysis.setCustomerId(customer.getCustomerId());
                rfmAnalysis.setRecency((int) recency);
                rfmAnalysis.setFrequency(frequency);
                rfmAnalysis.setMonetaryValue(monetary);
                rfmAnalysis.setRfmValue(rfmValue);
                rfmAnalysis.setSegment(segment);

                rfmAnalysisList.add(rfmAnalysis);
            }
        }

        // 存入資料表
        rfmAnalysisRepository.saveAll(rfmAnalysisList);
    }

    private String classifySegment(double rfmValue) {
        if (rfmValue > 0.8) {
            return "VIP客戶";
        } else if (rfmValue > 0.5) {
            return "一般客戶";
        } else {
            return "低價值客戶";
        }
    }
}
