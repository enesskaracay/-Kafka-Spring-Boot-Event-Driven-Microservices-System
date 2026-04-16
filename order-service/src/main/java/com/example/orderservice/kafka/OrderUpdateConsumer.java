package com.example.orderservice.kafka;

import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.event.PaymentProcessedEvent;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderUpdateConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment-processed", groupId = "order-update-group")
    public void consume(PaymentProcessedEvent event) {
        if ("PAID".equals(event.getStatus())) {
            System.out.println("✅ ORDER SERVICE: Ödeme onaylandı. Sipariş #" + event.getOrderId() + " tamamlanıyor.");
            // Enum'ın ismini (COMPLETED) String olarak servise gönderiyoruz
            orderService.updateOrderStatus(event.getOrderId(), OrderStatus.COMPLETED.name());
        }
        else if ("FAILED".equals(event.getStatus())) {
            System.out.println("🚨 ORDER SERVICE: Ödeme REDDEDİLDİ! Sipariş #" + event.getOrderId() + " iptal ediliyor.");
            // Enum'ın ismini (CANCELLED) String olarak servise gönderiyoruz
            orderService.updateOrderStatus(event.getOrderId(), OrderStatus.CANCELLED.name());
        }
    }
}