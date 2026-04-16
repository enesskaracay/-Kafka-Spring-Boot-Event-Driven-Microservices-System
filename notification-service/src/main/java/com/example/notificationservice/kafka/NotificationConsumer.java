package com.example.notificationservice.kafka;

import com.example.notificationservice.entity.NotificationHistory;
import com.example.notificationservice.event.PaymentProcessedEvent;
import com.example.notificationservice.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; // 🚀 Buraya da ekle!
import org.springframework.dao.DataIntegrityViolationException; // Bunu da ekle ki hata yakalama çalışsın

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationHistoryRepository repository;

    // 🚀 DİKKAT: ObjectMapper'ı sildik ve parametreyi direkt Obje olarak aldık.
    // Çünkü senin efsanevi application.yml dosyan çeviri işini hallediyor!
    @KafkaListener(topics = "payment-processed", groupId = "notification-group")
    public void consume(PaymentProcessedEvent event) {
        try {
            NotificationHistory history = new NotificationHistory();
            history.setOrderId(event.getOrderId());
            history.setMessage("Siparişiniz onaylandı: " + event.getOrderId());
            history.setSentAt(LocalDateTime.now());
            history.setStatus("SUCCESS");

            // 🛡️ IDEMPOTENCY: Her event'in benzersiz bir kimliği olmalı.
            // Şimdilik test için orderId + status kullanabilirsin veya event içinde UUID gönderebilirsin.
            history.setEventId("EVENT_" + event.getOrderId() + "_" + event.getStatus());

            repository.save(history);
            System.out.println("✅ NOTIFICATION KAYDEDİLDİ: Sipariş #" + event.getOrderId());

        } catch (DataIntegrityViolationException e) {
            // 🛡️ Eğer veritabanı "Bu ID zaten var" derse buraya düşer
            System.out.println("⚠️ DUPLICATE EVENT: Bu bildirim zaten işlenmiş, atlanıyor. OrderID: " + event.getOrderId());
        }
    }
}