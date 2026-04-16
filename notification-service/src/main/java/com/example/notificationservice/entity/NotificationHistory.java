package com.example.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import java.time.Instant;

@Entity
@Table(name = "notification_history")
@Data
public class NotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String message;
    private LocalDateTime sentAt;
    private String status;

    // 🚀 İŞTE KRİTİK ALAN:
    @Column(unique = true)
    private String eventId;


}