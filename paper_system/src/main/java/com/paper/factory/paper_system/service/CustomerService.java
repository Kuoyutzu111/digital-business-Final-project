package com.paper.factory.paper_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.Customer;
import com.paper.factory.paper_system.repository.CustomerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager entityManager;


   // @Transactional
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // 新增：获取所有客户
   // @Transactional
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // 新增：根据 ID 获取客户
   // @Transactional
    public Customer getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("找不到該顧客！"));
    }

    // 新增：更新客户
   // @Transactional
    public Customer updateCustomer(Integer customerId, Customer updatedCustomer) {
        Customer customer = getCustomerById(customerId); // 通过 ID 查找现有客户
        entityManager.refresh(customer);
        customer.setName(updatedCustomer.getName());
        customer.setAddress(updatedCustomer.getAddress());
        customer.setContactInfo(updatedCustomer.getContactInfo());
        return customerRepository.save(customer); // 保存更新后的客户
    }

    // 新增：删除客户
   // @Transactional
    public void deleteCustomer(Integer customerId) {
        customerRepository.deleteById(customerId);
    }
}
