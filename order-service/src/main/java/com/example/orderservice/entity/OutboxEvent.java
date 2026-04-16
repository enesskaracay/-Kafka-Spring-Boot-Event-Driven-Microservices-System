package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "outbox_events")
@Data
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId;
    private String eventType;
    @Column(length = 2000)
    private String payload;

    private String status = "PENDING";
    private int retryCount = 0;
    private Instant createdAt = Instant.now();
}