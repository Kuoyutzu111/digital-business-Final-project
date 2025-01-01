package com.paper.factory.paper_system.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paper.factory.paper_system.model.Material;
import com.paper.factory.paper_system.repository.MaterialRepository;
import com.paper.factory.paper_system.service.MaterialService;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;


    @Autowired
    private MaterialRepository materialRepository;

    // 获取所有原料
    @GetMapping
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @GetMapping("/inventory")
public ResponseEntity<List<Material>> getInventory() {
    List<Material> inventory = materialService.getAllMaterials();
    return ResponseEntity.ok(inventory);
}

    @PostMapping
    public Material addMaterial(@RequestBody Material material) {
        return materialService.saveMaterial(material);
    }

    @PutMapping("/{materialId}")
    public ResponseEntity<Material> updateMaterialStock(
            @PathVariable Integer materialId,
            @RequestBody Map<String, Integer> requestBody) {
        Integer newStockQuantity = requestBody.get("stockQuantity");
        if (newStockQuantity == null || newStockQuantity < 0) {
            return ResponseEntity.badRequest().build();
        }

        Material updatedMaterial = materialService.updateStock(materialId, newStockQuantity);
        if (updatedMaterial == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedMaterial);
    }
}

    

