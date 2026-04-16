package com.example.notificationservice.kafka;

import com.example.notificationservice.entity.NotificationHistory;
import com.example.notificationservice.event.PaymentProcessedEvent;
import com.example.notificationservice.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationHistoryRepository repository;

    // 🚀 DİKKAT: ObjectMapper'ı sildik ve parametreyi direkt Obje olarak aldık.
    // Çünkü senin efsanevi application.yml dosyan çeviri işini hallediyor!
    @KafkaListener(topics = "payment-processed", groupId = "notification-group")
    public void consume(PaymentProcessedEvent event) {
        try {
            // 1. Müşteriye gidecek metni hazırla
            String customerMessage;
            if ("PAID".equals(event.getStatus())) {
                customerMessage = "Tebrikler! Sipariş #" + event.getOrderId() + " ödemesi onaylandı. Kargonuz yola çıkmak üzere.";
            } else {
                customerMessage = "Üzgünüz. Sipariş #" + event.getOrderId() + " ödemesi reddedildi.";
            }

            // 2. Veritabanına kaydet
            NotificationHistory history = new NotificationHistory();
            history.setOrderId(event.getOrderId());
            history.setMessage(customerMessage);
            history.setStatus(event.getStatus());

            repository.save(history);

            // 3. Zafer Logu
            System.out.println("✅ NOTIFICATION KAYDEDİLDİ: " + customerMessage);

        } catch (Exception e) {
            System.out.println("🚨 NOTIFICATION HATA: " + e.getMessage());
        }
    }
}