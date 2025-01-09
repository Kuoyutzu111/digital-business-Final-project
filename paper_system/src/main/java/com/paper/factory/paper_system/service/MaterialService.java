package com.paper.factory.paper_system.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.Material;
import com.paper.factory.paper_system.model.OrderMaterialRequirement;
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
            Material updatedMaterial = materialRepository.save(material);
    
            // 仅在库存真正变动时检查是否需要更新安全门槛和建议补充数量
            updateReorderPointsIfNeeded();
            return updatedMaterial;
        }
        return null;
    }
    

    public void updateReorderPointsIfNeeded() {
        List<Material> materials = materialRepository.findAll();
    
        for (Material material : materials) {
            // 计算当前安全库存和建议补充量
            int currentReorderPoint = material.getReorderThreshold();
            int currentSuggestedReorderQuantity = calculateSuggestedReorderQuantity(material);
    
            // 获取新的安全库存和建议补充量
            List<Double> dailyDemands = getDailyDemands(material);
            double averageDailyDemand = calculateAverage(dailyDemands);
            double demandStdDev = calculateStandardDeviation(dailyDemands, averageDailyDemand);
            int leadTime = 3; // 前置时间
            double serviceLevel = 1.65; // 95%服务水平
    
            int newSafetyStock = (int) (serviceLevel * demandStdDev * Math.sqrt(leadTime));
            int newReorderPoint = (int) (leadTime * averageDailyDemand + newSafetyStock);
            int newSuggestedReorderQuantity = Math.max(0, newReorderPoint - material.getStockQuantity());
    
            // 如果安全库存或建议补充量发生变化，更新数据库
            if (currentReorderPoint != newReorderPoint || currentSuggestedReorderQuantity != newSuggestedReorderQuantity) {
                material.setReorderThreshold(newReorderPoint);
                material.setReorderQuantity(newSuggestedReorderQuantity);
                materialRepository.save(material);
            }
        }
    }
    

    public void updateStockAndReorderPoints() {
        List<Material> materials = materialRepository.findAll();

        for (Material material : materials) {
            List<Double> dailyDemands = getDailyDemands(material); // 根据历史数据计算平均需求
            double averageDailyDemand = calculateAverage(dailyDemands);
            double demandStdDev = calculateStandardDeviation(dailyDemands, averageDailyDemand); // 根据历史数据计算需求标准差
            int leadTime = 3; // 假设前置时间为3天
            double serviceLevel = 1.65; // 95%的服务水平对应Z值

            int safetyStock = (int) (serviceLevel * demandStdDev * Math.sqrt(leadTime));
            int reorderPoint = (int) (leadTime * averageDailyDemand + safetyStock);

            material.setReorderThreshold(reorderPoint);
            material.setReorderQuantity(reorderPoint - material.getStockQuantity());

            materialRepository.save(material);
        }
    }
    private List<Double> getDailyDemands(Material material) {
        List<OrderMaterialRequirement> requirements = 
            orderMaterialRequirementRepository.findByMaterialId(material.getMaterialId());

        Map<LocalDate, Double> dailyDemandMap = new HashMap<>();
        for (OrderMaterialRequirement requirement : requirements) {
            LocalDate orderDate = requirement.getOrder().getOrderDate();
            double requiredQuantity = requirement.getTotalRequiredQuantity();

            dailyDemandMap.merge(orderDate, requiredQuantity, Double::sum); // 按日期累加需求
        }
        return new ArrayList<>(dailyDemandMap.values());
    }

    private double calculateAverage(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double calculateStandardDeviation(List<Double> values, double mean) {
        double variance = values.stream()
                .mapToDouble(value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    private int calculateSuggestedReorderQuantity(Material material) {
        int reorderPoint = material.getReorderThreshold();
        int currentStock = material.getStockQuantity();
        return Math.max(0, reorderPoint - currentStock);
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public List<Map<String, Object>> getMaterialWithReorderData() {
        return materialRepository.findAll().stream().map(material -> {
            Map<String, Object> materialData = new HashMap<>();
            materialData.put("materialId", material.getMaterialId());
            materialData.put("name", material.getName());
            materialData.put("stockQuantity", material.getStockQuantity());
            materialData.put("reorderThreshold", material.getReorderThreshold());
            materialData.put("suggestedReorderQuantity", calculateSuggestedReorderQuantity(material));
            return materialData;
        }).collect(Collectors.toList());
    }

    public Material saveMaterial(Material material) {
        return materialRepository.save(material);
    }

    public List<Map<String, Object>> getMaterialOrderDetails(Integer materialId) {
        return orderMaterialRequirementRepository.findByMaterialId(materialId).stream().map(requirement -> {
            Map<String, Object> detail = new HashMap<>();
            detail.put("orderId", requirement.getOrder().getOrderId());
            detail.put("orderDate", requirement.getOrder().getOrderDate());
            detail.put("requiredQuantity", requirement.getTotalRequiredQuantity());
            return detail;
        }).collect(Collectors.toList());
    }
}


