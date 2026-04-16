package com.example.paymentservice.kafka;

import com.example.paymentservice.entity.ProcessedEvent;
import com.example.paymentservice.event.OrderCreatedEvent;
import com.example.paymentservice.event.PaymentProcessedEvent;
import com.example.paymentservice.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper; // 🚀 YENİ EKLENDİ
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper; // 🚀 ÇEVİRMENİMİZ

    // 🚀 DİKKAT: Artık parametre olarak OrderCreatedEvent değil, String (düz metin) alıyoruz
    @KafkaListener(topics = "order-created", groupId = "payment-group")
    @Transactional
    public void consume(String payload) {
        try {
            // 0. ÇEVİRİ İŞLEMİ (Metni Java Objesine Çeviriyoruz)
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);

            // 1. IDEMPOTENCY KONTROLÜ
            if (processedEventRepository.existsByOrderId(event.getOrderId())) {
                System.out.println("⚠️ PAYMENT SERVICE: Sipariş #" + event.getOrderId() +
                        " zaten işlenmiş. Mükerrer istek reddedildi.");
                return;
            }

            System.out.println("PAYMENT SERVICE: Yeni sipariş işleniyor, ID: " + event.getOrderId());

            // 2. ÖDEME MANTIĞI
            String paymentStatus;
            if (event.getPrice() > 10000) {
                paymentStatus = "FAILED";
            } else {
                paymentStatus = "PAID";
            }

            // 3. SONUCU KAFKA'YA FIRLAT
            PaymentProcessedEvent processedEvent = new PaymentProcessedEvent(
                    event.getOrderId(),
                    paymentStatus,
                    Instant.now()
            );

            kafkaTemplate.send("payment-processed", processedEvent.getOrderId().toString(), processedEvent);

            // 4. İŞLENDİ OLARAK KAYDET (HAFIZAYA AL)
            processedEventRepository.save(new ProcessedEvent(event.getOrderId(), Instant.now()));

            System.out.println("✅ PAYMENT SERVICE: İşlem tamamlandı ve hafızaya alındı. Durum: " + paymentStatus);

        } catch (Exception e) {
            System.out.println("🚨 PAYMENT SERVICE HATA: Gelen mesaj çevrilemedi! " + e.getMessage());
        }
    }
}