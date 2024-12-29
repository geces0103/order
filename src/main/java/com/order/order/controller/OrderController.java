package com.order.order.controller;

import com.order.order.model.Order;
import com.order.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Endpoint to create a new order.
     * @param order The order to be created.
     * @return The created order.
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        log.info("Request received to create order: {}", order);
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    /**
     * Endpoint to fetch all orders.
     * @return List of all orders.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("Request received to fetch all orders");
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Endpoint to fetch an order by ID.
     * @param id The ID of the order.
     * @return The order if found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        log.info("Request received to fetch order by ID: {}", id);
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to process a batch of high-volume orders.
     * @param orders List of orders to be processed.
     * @return Success message after processing.
     */
    @PostMapping("/process-batch")
    public ResponseEntity<String> processHighVolumeOrders(@RequestBody Set<Order> orders) {
        log.info("Request received to process batch of orders: {}", orders.size());
        orderService.processHighVolumeOrders(orders);
        return ResponseEntity.ok("Batch processing completed successfully");
    }
}
