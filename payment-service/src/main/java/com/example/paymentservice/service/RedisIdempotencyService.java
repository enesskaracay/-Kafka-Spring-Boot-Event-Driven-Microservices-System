package com.example.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisIdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "payment:idempotency:event:";

    public boolean canProcess(String eventId) {
        String key = KEY_PREFIX + eventId;

        // 1. SET NX EX: Atomic olarak kilit at (5 dakika PROCESSING süresi)
        // Eğer key yoksa set eder ve true döner. Varsa false döner.
        Boolean isLockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(key, "PROCESSING", Duration.ofMinutes(5));

        if (Boolean.FALSE.equals(isLockAcquired)) {
            String currentStatus = redisTemplate.opsForValue().get(key);
            if ("COMPLETED".equals(currentStatus)) {
                log.warn("Event {} zaten başarıyla işlendi, skip ediliyor.", eventId);
                return false;
            }
            log.info("Event {} şu an başka bir instance tarafından işleniyor.", eventId);
            return false;
        }
        return true;
    }

    public void markAsCompleted(String eventId) {
        // İşlem bitti, kilidi 24 saatlik COMPLETED statüsüne çevir
        redisTemplate.opsForValue().set(KEY_PREFIX + eventId, "COMPLETED", Duration.ofHours(24));
    }
}