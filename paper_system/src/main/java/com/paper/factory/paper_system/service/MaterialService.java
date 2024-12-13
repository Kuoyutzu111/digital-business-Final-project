package com.paper.factory.paper_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.Material;
import com.paper.factory.paper_system.repository.MaterialRepository;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public Material saveMaterial(Material material) {
        return materialRepository.save(material);
    }
}
