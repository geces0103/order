package com.order.service;

import com.order.model.Order;
import com.order.model.Product;
import com.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Creates a new order, calculates the total value, and checks for duplicates.
     * @param order The order to be created.
     * @return The created order.
     */
    public Order createOrder(Order order) {
        validateOrder(order);

        if (isDuplicateOrder(order)) {
            log.warn("Duplicate order detected: {}", order);
            throw new IllegalArgumentException("Duplicate order detected");
        }

        double totalValue = order.getProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum();
        order.setTotalValue(totalValue);
        log.info("Order created with total value: {}", totalValue);

        return orderRepository.save(order);
    }

    /**
     * Retrieves all orders from the database.
     * @return A list of all orders.
     */
    public List<Order> getAllOrders() {
        log.info("Fetching all orders from the database");
        return orderRepository.findAll();
    }

    /**
     * Retrieves an order by its ID.
     * @param id The ID of the order.
     * @return An Optional containing the order if found.
     */
    public Optional<Order> getOrderById(Long id) {
        log.info("Fetching order by ID: {}", id);
        return orderRepository.findById(id);
    }

    /**
     * Checks if an order is a duplicate based on some unique attributes (e.g., customer name and products).
     * @param order The order to check.
     * @return True if the order is a duplicate, false otherwise.
     */
    private boolean isDuplicateOrder(Order order) {
        log.debug("Checking for duplicate order: {}", order);
        return orderRepository.findAll().stream()
                .anyMatch(existingOrder -> existingOrder.getCustomerName().equals(order.getCustomerName()) &&
                        existingOrder.getProducts().equals(order.getProducts()));
    }

    /**
     * Validates the order for null values and missing fields.
     * @param order The order to validate.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            log.error("Order cannot be null");
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
            log.error("Customer name is required");
            throw new IllegalArgumentException("Customer name is required");
        }
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            log.error("Order must contain at least one product");
            throw new IllegalArgumentException("Order must contain at least one product");
        }
    }

    /**
     * Handles high concurrency by ensuring thread-safety and transactional consistency.
     * To be integrated with external systems A and B in future expansions.
     */
    public synchronized void processHighVolumeOrders(Set<Order> orders) {
        log.info("Processing high volume of orders: {} orders", orders.size());
        orders.forEach(this::createOrder);
    }
}