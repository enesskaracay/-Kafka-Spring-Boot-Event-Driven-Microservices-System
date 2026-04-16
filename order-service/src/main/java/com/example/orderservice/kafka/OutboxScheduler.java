package com.example.orderservice.kafka;

import com.example.orderservice.entity.OutboxEvent;
import com.example.orderservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Her 5 saniyede bir çalışır
    @Scheduled(fixedRate = 5000)
    public void processOutboxEvents() {
        // Sadece "PENDING" olanları al
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus("PENDING");

        for (OutboxEvent event : pendingEvents) {
            try {
                // 1. Kafka'ya göndermeyi DENE
                kafkaTemplate.send(event.getEventType(), event.getAggregateId(), event.getPayload());

                // 2. Başarılı olursa durumu güncelle
                event.setStatus("COMPLETED");
                System.out.println("✅ OUTBOX: Mesaj başarıyla Kafka'ya iletildi. ID: " + event.getId());

            } catch (Exception e) {
                // 3. HATA ALIRSA BURAYA DÜŞER (Retry Mantığı)
                event.setRetryCount(event.getRetryCount() + 1);
                System.out.println("⚠️ OUTBOX HATA: Kafka'ya ulaşılamadı. Deneme: " + event.getRetryCount() + "/3");

                // Eğer 3 kere denediysek ve hala olmuyorsa, DLQ'ya (FAILED) çek
                if (event.getRetryCount() >= 3) {
                    event.setStatus("FAILED");
                    System.out.println("🚨 KRİTİK HATA: Mesaj Kafka'ya iletilemedi. FAILED (DLQ) statüsüne çekildi! ID: " + event.getId());
                }
            }

            // Değişiklikleri veritabanına kaydet
            outboxEventRepository.save(event);
        }
    }
}