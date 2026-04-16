package com.example.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "notification_history")
@Data
public class NotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Column(length = 1000)
    private String message; // Müşteriye giden asıl mesaj

    private String status; // Başarılı mı, Başarısız mı?

    private Instant sentAt = Instant.now();
}