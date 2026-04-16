package com.example.paymentservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "processed_events")
@Data // Getter, Setter, toString, equals ve hashCode metotlarını otomatik oluşturur
@NoArgsConstructor // Boş constructor (JPA için zorunludur)
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🚀 ZIRH BURASI: Aynı sipariş ID'si bu tabloya ikinci kez GİREMEZ!
    @Column(unique = true, nullable = false)
    private Long orderId;

    private Instant processedAt;

    // Consumer'da kullandığın 'new ProcessedEvent(orderId, time)' için özel constructor
    public ProcessedEvent(Long orderId, Instant processedAt) {
        this.orderId = orderId;
        this.processedAt = processedAt;
    }
}