package com.example.paymentservice.kafka;

import com.example.paymentservice.entity.ProcessedEvent;
import com.example.paymentservice.event.OrderCreatedEvent;
import com.example.paymentservice.event.PaymentProcessedEvent;
import com.example.paymentservice.repository.ProcessedEventRepository;
import com.example.paymentservice.service.RedisIdempotencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;
    private final RedisIdempotencyService redisIdempotencyService;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    @Transactional
    public void consume(String payload) {
        try {
            // 🚀 1. GELİŞMİŞ TEMİZLİK OPERASYONU (Tırnak ve Kaçış Karakterlerini Temizler)
            String cleanPayload = payload.trim();

            // Eğer veri '"{\"...\"}"' formatında gelirse, içteki saf JSON'ı çıkarır
            if (cleanPayload.startsWith("\"") && cleanPayload.endsWith("\"")) {
                cleanPayload = cleanPayload.substring(1, cleanPayload.length() - 1).replace("\\\"", "\"");
            }

            // Eğer temizlik sonrası hala başında/sonunda tırnak kalmışsa Jackson ile bir tur daha oku
            if (cleanPayload.startsWith("\"")) {
                cleanPayload = objectMapper.readValue(cleanPayload, String.class);
            }

            OrderCreatedEvent event = objectMapper.readValue(cleanPayload, OrderCreatedEvent.class);
            String eventId = event.getOrderId().toString();

            // 🚀 2. REDIS IDEMPOTENCY GUARD (İLK SAVUNMA HATTI)
            try {
                if (!redisIdempotencyService.canProcess(eventId)) {
                    log.warn("⚠️ Mesaj zaten işlenmiş veya süreçte: {}", eventId);
                    return;
                }
            } catch (Exception e) {
                log.error("⚠️ Redis Down! DB Fallback devreye giriyor. EventId: {}", eventId);
                if (processedEventRepository.existsByOrderId(event.getOrderId())) {
                    log.warn("⚠️ Mükerrer istek DB Fallback ile engellendi. Sipariş #{}", event.getOrderId());
                    return;
                }
            }

            log.info("💳 PAYMENT SERVICE: İşlem başlıyor. Sipariş #{}", event.getOrderId());

            // 🚀 3. ÖDEME MANTIĞI
            String paymentStatus = (event.getPrice() > 10000) ? "FAILED" : "PAID";

            // 🚀 4. SONUCU KAFKA'YA FIRLAT
            PaymentProcessedEvent processedEvent = new PaymentProcessedEvent(
                    event.getOrderId(),
                    paymentStatus,
                    Instant.now()
            );
            kafkaTemplate.send("payment-processed", processedEvent.getOrderId().toString(), processedEvent);

            // 🚀 5. DB KAYDI (SOURCE OF TRUTH)
            processedEventRepository.save(new ProcessedEvent(event.getOrderId(), Instant.now()));

            // 🚀 6. REDIS STATE UPDATE (BEST EFFORT)
            try {
                redisIdempotencyService.markAsCompleted(eventId);
            } catch (Exception e) {
                log.error("🚨 Redis update hatası: {}. Ancak DB kaydı başarılı.", e.getMessage());
            }

            log.info("✅ PAYMENT SERVICE: İşlem tamamlandı. Sipariş #{}, Durum: {}", event.getOrderId(), paymentStatus);

        } catch (Exception e) {
            log.error("🚨 PAYMENT SERVICE KRİTİK HATA: Mesaj işlenemedi! Veri: {}, Hata: {}", payload, e.getMessage());
            throw new RuntimeException("İşlem başarısız, retry tetikleniyor...");
        }
    }
}