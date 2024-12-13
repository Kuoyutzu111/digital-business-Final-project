package com.paper.factory.paper_system.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paper.factory.paper_system.model.BOM;
import com.paper.factory.paper_system.service.BOMService;

@RestController
@RequestMapping("/boms")
public class BOMController {

    @Autowired
    private BOMService bomService;

    @GetMapping
    public List<BOM> getAllBOMs() {
        return bomService.getAllBOMs();
    }

    @PostMapping
    public BOM addBOM(@RequestBody BOM bom) {
        return bomService.saveBOM(bom);
    }
}

