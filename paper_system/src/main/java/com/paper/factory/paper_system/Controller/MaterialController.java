package com.paper.factory.paper_system.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paper.factory.paper_system.model.Material;
import com.paper.factory.paper_system.service.MaterialService;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping
    public List<Material> getAllMaterials() {
        return materialService.getAllMaterials();
    }

    @PostMapping
    public Material addMaterial(@RequestBody Material material) {
        return materialService.saveMaterial(material);
    }
}
