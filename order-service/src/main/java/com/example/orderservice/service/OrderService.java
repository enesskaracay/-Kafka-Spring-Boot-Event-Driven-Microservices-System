package com.example.orderservice.service;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OutboxEvent;
import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository; // Adını orderRepository yapmıştık
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        System.out.println("CREATE ORDER ÇALIŞTI (OUTBOX PATTERN İLE)");

        // 1. Siparişi veritabanına yaz
        Order order = new Order();
        order.setProduct(request.getProduct());
        order.setPrice(request.getPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);

        // 2. Kafka'ya gidecek Event objesini hazırla
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getProduct(),
                savedOrder.getPrice(),
                savedOrder.getStatus().name(),
                savedOrder.getCreatedAt()
        );

        try {
            // 3. Event'i JSON String'e çevir (Payload)
            String eventPayload = objectMapper.writeValueAsString(event);

            // 4. Outbox Tablosuna "PENDING" olarak yaz
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateId(savedOrder.getId().toString());
            outboxEvent.setEventType("order-created");
            outboxEvent.setPayload(eventPayload);
            outboxEvent.setStatus("PENDING");
            outboxEvent.setCreatedAt(Instant.now());

            outboxEventRepository.save(outboxEvent);
            System.out.println("EVENT OUTBOX TABLOSUNA YAZILDI (Kafka'ya kurye götürecek)");

        } catch (Exception e) {
            throw new RuntimeException("Outbox Event oluşturulurken hata: " + e.getMessage());
        }

        return savedOrder;
    }

    // --- ESKİDEN VAR OLAN VE SİLİNEN METODLAR EKLENDİ ---

    // Kafka'dan gelen ödeme sonucuna göre DB'yi güncelleyen metod
    public void updateOrderStatus(Long orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.valueOf(status));
            orderRepository.save(order);
            System.out.println("ORDER SERVICE: Sipariş #" + orderId + " durumu veritabanında güncellendi: " + status);
        });
    }

    // Tüm siparişleri getiren metod
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}