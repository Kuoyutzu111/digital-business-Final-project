import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@SpringBootApplication
public class OrdersystemBackend {
    public static void main(String[] args) {
        SpringApplication.run(OrdersystemBackend.class, args);
    }
}

// MODEL LAYER
@Entity
class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String contact;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}

@Entity
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String status;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}

@Entity
class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private Integer quantity;

    @ManyToOne
    private Order order;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}

// REPOSITORY LAYER
interface CustomerRepository extends JpaRepository<Customer, Long> {}
interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :fromDate AND :toDate")
    List<Order> findOrdersWithinDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}

// SERVICE LAYER
@Service
class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() { return customerRepository.findAll(); }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer createCustomer(Customer customer) { return customerRepository.save(customer); }
}

@Service
class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() { return orderRepository.findAll(); }

    public Order createOrder(Order order) { return orderRepository.save(order); }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}

@Service
class RFMAnalysisService {
    @Autowired
    private OrderRepository orderRepository;

    public List<RFMResult> calculateRFM(LocalDate fromDate, LocalDate toDate) {
        List<Order> orders = orderRepository.findOrdersWithinDateRange(fromDate, toDate);

        Map<Long, RFMResult> rfmResults = new HashMap<>();
        for (Order order : orders) {
            Long customerId = order.getCustomer().getId();
            RFMResult rfm = rfmResults.getOrDefault(customerId, new RFMResult(customerId));

            rfm.updateRecency(order.getOrderDate());
            rfm.incrementFrequency();
            rfm.addMonetary(BigDecimal.valueOf(order.getItems().stream().mapToDouble(item -> item.getQuantity()).sum()));

            rfmResults.put(customerId, rfm);
        }

        return new ArrayList<>(rfmResults.values());
    }
}

class RFMResult {
    private Long customerId;
    private int recency = Integer.MAX_VALUE;
    private int frequency = 0;
    private BigDecimal monetary = BigDecimal.ZERO;

    public RFMResult(Long customerId) { this.customerId = customerId; }

    public void updateRecency(LocalDate orderDate) {
        this.recency = Math.min(this.recency, Period.between(orderDate, LocalDate.now()).getDays());
    }

    public void incrementFrequency() { this.frequency++; }

    public void addMonetary(BigDecimal amount) { this.monetary = this.monetary.add(amount); }

    // Getters
    public Long getCustomerId() { return customerId; }
    public int getRecency() { return recency; }
    public int getFrequency() { return frequency; }
    public BigDecimal getMonetary() { return monetary; }
}

// CONTROLLER LAYER
@RestController
@RequestMapping("/api/customers")
class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers() { return customerService.getAllCustomers(); }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) { return customerService.getCustomerById(id); }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) { return customerService.createCustomer(customer); }
}

@RestController
@RequestMapping("/api/orders")
class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() { return orderService.getAllOrders(); }

    @PostMapping
    public Order createOrder(@RequestBody Order order) { return orderService.createOrder(order); }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) { return orderService.getOrderById(id); }
}

@RestController
@RequestMapping("/api/rfm-analysis")
class RFMController {
    @Autowired
    private RFMAnalysisService rfmAnalysisService;

    @GetMapping
    public List<RFMResult> getRFMAnalysis(@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
        return rfmAnalysisService.calculateRFM(fromDate, toDate);
    }
}
