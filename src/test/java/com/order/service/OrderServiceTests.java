package com.order.service;

import com.order.model.Order;
import com.order.model.Product;
import com.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTests {

    private OrderRepository orderRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderService(orderRepository);
    }

    @Test
    void testCreateOrderSuccess() {
        Product product = new Product();
        product.setName("Product A");
        product.setPrice(50.0);

        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setProducts(Collections.singletonList(product));

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals(50.0, createdOrder.getTotalValue());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCreateOrderFailsWithDuplicate() {
        Product product = new Product();
        product.setName("Product A");
        product.setPrice(50.0);

        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setProducts(Collections.singletonList(product));

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(order));
        assertEquals("Duplicate order detected", exception.getMessage());
    }

    @Test
    void testCreateOrderFailsWithInvalidData() {
        Order invalidOrder = new Order();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(invalidOrder));
        assertEquals("Customer name is required", exception.getMessage());
    }

    @Test
    void testGetOrderById() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("John Doe");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> foundOrder = orderService.getOrderById(1L);

        assertTrue(foundOrder.isPresent());
        assertEquals("John Doe", foundOrder.get().getCustomerName());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllOrders() {
        Order order1 = new Order();
        order1.setCustomerName("John Doe");

        Order order2 = new Order();
        order2.setCustomerName("Jane Doe");

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        var orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findAll();
    }
}

