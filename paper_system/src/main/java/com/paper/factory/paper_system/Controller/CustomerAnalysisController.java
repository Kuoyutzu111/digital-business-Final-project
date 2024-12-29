package com.paper.factory.paper_system.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paper.factory.paper_system.service.CustomerAnalysisService;

@RestController
@RequestMapping("/api/customer-analysis")
public class CustomerAnalysisController {

    @Autowired
    private CustomerAnalysisService customerAnalysisService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCustomerAnalysis() {
        List<Map<String, Object>> analysisResults = customerAnalysisService.calculateCustomerAnalysis();
        return ResponseEntity.ok(analysisResults);
    }
}
