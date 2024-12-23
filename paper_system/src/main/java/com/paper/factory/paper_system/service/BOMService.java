package com.paper.factory.paper_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.BOM;
import com.paper.factory.paper_system.repository.BOMRepository;

@Service
public class BOMService {

    @Autowired
    private BOMRepository bomRepository;

    public List<BOM> getAllBOMs() {
        return bomRepository.findAll();
    }

    public BOM saveBOM(BOM bom) {
        return bomRepository.save(bom);
    }

    public BOM updateBOM(Integer bomId, Double newRequiredQuantity) {
        BOM bom = bomRepository.findById(bomId)
                .orElseThrow(() -> new RuntimeException("BOM ID: " + bomId + " 不存在"));
        bom.setRequiredQuantity(newRequiredQuantity);
        return bomRepository.save(bom);
    }
}
