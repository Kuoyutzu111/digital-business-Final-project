package com.paper.factory.paper_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.Material;
import com.paper.factory.paper_system.repository.MaterialRepository;
import com.paper.factory.paper_system.repository.OrderMaterialRequirementRepository;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private OrderMaterialRequirementRepository orderMaterialRequirementRepository;

    public List<Material> calculateInventoryFromRequirements() {
        List<Material> materials = materialRepository.findAll();

        for (Material material : materials) {
            // 根據需求計算
            Double totalRequired = orderMaterialRequirementRepository
                .findTotalRequiredByMaterialId(material.getMaterialId());

            // 更新庫存數量（僅計算差異）
            material.setStockQuantity(material.getStockQuantity() - totalRequired.intValue());

            materialRepository.save(material);
        }

        return materials;
    }

    public Material updateStock(Integer materialId, Integer newStockQuantity) {
        Optional<Material> materialOptional = materialRepository.findById(materialId);
        if (materialOptional.isPresent()) {
            Material material = materialOptional.get();
            material.setStockQuantity(newStockQuantity);
            return materialRepository.save(material);
        }
        return null;
    }
    

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public Material saveMaterial(Material material) {
        return materialRepository.save(material);
    }
}
