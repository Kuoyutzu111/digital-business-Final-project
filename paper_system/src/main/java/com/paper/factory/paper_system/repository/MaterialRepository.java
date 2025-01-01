package com.paper.factory.paper_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paper.factory.paper_system.model.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    // 根據名稱查找 Material
    Optional<Material> findByName(String name);
}
