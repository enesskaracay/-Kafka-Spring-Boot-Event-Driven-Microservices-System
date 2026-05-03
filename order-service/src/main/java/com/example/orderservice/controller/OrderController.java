package com.example.orderservice.controller;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse; // 🚀 Yeni eklendi
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService; // 🚀 Interface olanı kullanıyoruz
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; // 🚀 Constructor injection için
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor // 🚀 @Autowired yerine constructor injection (Sektör Standardı)
public class OrderController {

    private final OrderService orderService; // 🚀 Interface üzerinden iletişim

    // GET /orders -> Tüm siparişleri getir (Liste Cache tetiklenir)
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // GET /orders/{id} -> Tekil sipariş getir (Detay Cache tetiklenir)
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // POST /orders -> Yeni sipariş ekle (Outbox yazar & Liste Cache'i temizler)
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }
}