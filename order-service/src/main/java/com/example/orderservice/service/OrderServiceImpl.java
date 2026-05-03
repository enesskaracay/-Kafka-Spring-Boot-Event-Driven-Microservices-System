package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.entity.OutboxEvent;
import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @CacheEvict(value = "orderListCache", allEntries = true)
    public Order createOrder(CreateOrderRequest request) {
        log.info("Sipariş oluşturuluyor: {}", request.getProduct());

        Order order = new Order();
        order.setProduct(request.getProduct());
        order.setPrice(request.getPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getProduct(),
                savedOrder.getPrice(),
                savedOrder.getStatus().name(),
                savedOrder.getCreatedAt()
        );

        try {
            String eventPayload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateId(savedOrder.getId().toString());
            outboxEvent.setEventType("order-created");
            outboxEvent.setPayload(eventPayload);
            outboxEvent.setStatus("PENDING");
            outboxEvent.setCreatedAt(Instant.now());
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Outbox Hatası: {}", e.getMessage());
            throw new RuntimeException("Outbox oluşturulamadı");
        }

        return savedOrder;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orderCache", key = "#orderId"),
            @CacheEvict(value = "orderListCache", allEntries = true)
    })
    public void updateOrderStatus(Long orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.valueOf(status));
            orderRepository.save(order);
            log.info("Sipariş durumu güncellendi ve Cache temizlendi: #{}", orderId);
        });
    }

    @Override
    @Cacheable(value = "orderListCache", key = "'all_orders'")
    public List<OrderResponse> getAllOrders() {
        log.info("📡 DB'den okunuyor: Tüm Siparişler");
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "orderCache", key = "#id")
    public OrderResponse getOrderById(Long id) {
        log.info("📡 DB'den okunuyor: Sipariş Detay #{}", id);
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getProduct(),
                order.getPrice(),
                order.getStatus().name()
        );
    }
}