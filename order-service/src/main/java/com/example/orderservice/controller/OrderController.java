package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.orderservice.dto.CreateOrderRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // GET /orders -> tüm siparişleri getir
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // POST /orders -> yeni sipariş ekle ve Kafka'ya gönder
    @PostMapping
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}