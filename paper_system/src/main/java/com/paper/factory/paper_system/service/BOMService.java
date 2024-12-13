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
}
