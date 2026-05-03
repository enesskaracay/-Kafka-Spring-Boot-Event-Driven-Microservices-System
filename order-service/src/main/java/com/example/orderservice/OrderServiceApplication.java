package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // 🚀 Önbellekleme için
import org.springframework.scheduling.annotation.EnableScheduling; // 🚀 İŞTE EKSİK OLAN IMPORT BURADA!

@EnableCaching // 🛡️ Önbellekleme mekanizmasını ateşle!
@EnableScheduling // ⏱️ Zamanlanmış görevleri (Outbox) ateşle!
@SpringBootApplication
public class OrderServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}
}