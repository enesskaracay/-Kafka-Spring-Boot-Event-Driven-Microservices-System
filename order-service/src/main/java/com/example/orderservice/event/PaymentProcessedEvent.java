package com.example.orderservice.event; // Paket ismine dikkat!

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent {
    private Long orderId;
    private String status;
    private Instant processedAt;
}