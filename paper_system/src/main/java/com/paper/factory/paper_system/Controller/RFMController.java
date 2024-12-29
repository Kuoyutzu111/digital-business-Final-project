package com.paper.factory.paper_system.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paper.factory.paper_system.model.RFMAnalysis;
import com.paper.factory.paper_system.service.RFMService;

@RestController
@RequestMapping("/api/rfm-analysis")
public class RFMController {

    @Autowired
    private RFMService rfmService;

    @GetMapping
    public ResponseEntity<List<RFMAnalysis>> getRFMResults() {
        List<RFMAnalysis> rfmResults = rfmService.getRFMResults();
        return ResponseEntity.ok(rfmResults);
    }

    @PostMapping
    public ResponseEntity<String> calculateAndSaveRFM() {
        rfmService.calculateAndSaveRFM();
        return ResponseEntity.ok("RFM 分析計算完成並已保存");
    }
}
