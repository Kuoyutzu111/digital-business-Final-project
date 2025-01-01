package com.paper.factory.paper_system.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.BOM;
import com.paper.factory.paper_system.model.Employee;
import com.paper.factory.paper_system.model.Material;
import com.paper.factory.paper_system.model.Product;
import com.paper.factory.paper_system.repository.BOMRepository;
import com.paper.factory.paper_system.repository.EmployeeRepository;
import com.paper.factory.paper_system.repository.MaterialRepository;
import com.paper.factory.paper_system.repository.ProductRepository;

import jakarta.annotation.PostConstruct;

@Service
public class DataInitializer {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private BOMRepository bomRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RFMService rfmService;


    @PostConstruct
    public void init() {
        initEmployees();
        initMaterialsAndProducts();
    }

    private void initEmployees() {
        addEmployeeIfNotExist(
                "陳大明",
                "M2019001",
                "112233",
                "Marketing",
                "文員");

        addEmployeeIfNotExist(
                "吳小美",
                "P2022003",
                "998877",
                "Production",
                "主管");
    }

    private void addEmployeeIfNotExist(String name, String username, String rawPassword, String department,
            String role) {
        if (!employeeRepository.existsByUsername(username)) {
            Employee employee = new Employee(
                    name,
                    username,
                    passwordEncoder.encode(rawPassword),
                    department,
                    role);
            employeeRepository.save(employee);
        }
    }

    private void initMaterialsAndProducts() {
        if (materialRepository.count() == 0) {
            // 初始化原材料
            Material woodPulp = new Material(null, "木漿", 10000, 1000, 2000);
            Material water = new Material(null, "水", 5000, 500, 1000);
            Material softener = new Material(null, "柔軟劑", 1000, 100, 200);
            Material absorbent = new Material(null, "吸水劑", 500, 50, 100);
    
            materialRepository.saveAll(Arrays.asList(woodPulp, water, softener, absorbent));
        }
    
        if (productRepository.count() == 0) {
            // 初始化產品
            Product toiletPaper = new Product(null, "衛生紙", 50.0, "24包");
            Product tissue = new Product(null, "面紙", 60.0, "24盒");
            Product kitchenTowel = new Product(null, "廚房紙巾", 80.0, "12卷");
            Product handTowel = new Product(null, "擦手紙", 70.0, "20包");
    
            productRepository.saveAll(Arrays.asList(toiletPaper, tissue, kitchenTowel, handTowel));
    
            // 初始化 BOM
            Material woodPulp = materialRepository.findByName("木漿").orElseThrow(
                    () -> new IllegalArgumentException("木漿未找到，請檢查初始化邏輯"));
            Material water = materialRepository.findByName("水").orElseThrow(
                    () -> new IllegalArgumentException("水未找到，請檢查初始化邏輯"));
            Material softener = materialRepository.findByName("柔軟劑").orElseThrow(
                    () -> new IllegalArgumentException("柔軟劑未找到，請檢查初始化邏輯"));
            Material absorbent = materialRepository.findByName("吸水劑").orElseThrow(
                    () -> new IllegalArgumentException("吸水劑未找到，請檢查初始化邏輯"));
    
            bomRepository.saveAll(Arrays.asList(
                    new BOM(null, toiletPaper, woodPulp, 960.0),
                    new BOM(null, toiletPaper, water, 120.0),
                    new BOM(null, toiletPaper, softener, 48.0),
    
                    new BOM(null, tissue, woodPulp, 3600.0),
                    new BOM(null, tissue, water, 240.0),
                    new BOM(null, tissue, softener, 120.0),
    
                    new BOM(null, kitchenTowel, woodPulp, 2400.0),
                    new BOM(null, kitchenTowel, water, 180.0),
                    new BOM(null, kitchenTowel, absorbent, 120.0),
    
                    new BOM(null, handTowel, woodPulp, 3200.0),
                    new BOM(null, handTowel, water, 300.0),
                    new BOM(null, handTowel, absorbent, 100.0)
            ));
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initRFMAnalysis() {
        rfmService.calculateAndSaveRFM();
    }
    
}
