package com.example.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
        // 1. RECOVERER: Mesaj tamamen patlarsa ne yapalım?
        // Cevap: Otomatik olarak "orijinal-topic.DLT" adında bir çöplük topic'ine yolla.
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);

        // 2. BACKOFF (Geri Sekme): Hemen pes etme!
        // 2000L = 2 saniye bekle. 3 = Toplamda 3 kere tekrar dene.
        FixedBackOff backOff = new FixedBackOff(2000L, 3);

        // Kalkanı oluştur ve Spring'e teslim et
        return new DefaultErrorHandler(recoverer, backOff);
    }
}