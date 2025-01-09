package com.paper.factory.paper_system.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.paper.factory.paper_system.model.OrderMaterialRequirement;
import com.paper.factory.paper_system.repository.MaterialRepository;
import com.paper.factory.paper_system.repository.OrderMaterialRequirementRepository;
import com.paper.factory.paper_system.service.MaterialService;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;


    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private OrderMaterialRequirementRepository orderMaterialRequirementRepository;

    // 获取所有原料
    @GetMapping
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @GetMapping("/inventory")
public ResponseEntity<List<Map<String, Object>>> getInventory() {
    List<Map<String, Object>> inventory = materialService.getMaterialWithReorderData();
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

    @GetMapping("/{materialId}/details")
    public ResponseEntity<List<Map<String, Object>>> getMaterialDetails(@PathVariable Integer materialId) {
        List<Map<String, Object>> details = materialService.getMaterialOrderDetails(materialId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{materialId}/orders")
    public ResponseEntity<List<Map<String, Object>>> getOrdersByMaterialId(@PathVariable Integer materialId) {
        List<OrderMaterialRequirement> requirements = orderMaterialRequirementRepository.findByMaterialId(materialId);

        if (requirements.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> orders = requirements.stream().map(req -> {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderId", req.getOrder().getOrderId());
            orderData.put("orderDate", req.getOrder().getOrderDate());
            orderData.put("requiredQuantity", req.getTotalRequiredQuantity());
            return orderData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }
    

}

    

