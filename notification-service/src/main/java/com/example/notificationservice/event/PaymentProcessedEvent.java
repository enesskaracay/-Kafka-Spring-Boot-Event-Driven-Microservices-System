package com.example.notificationservice.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Fazladan alan gelirse çökmesin
public class PaymentProcessedEvent {
    private Long orderId;
    private String status;
    private Instant processedAt;
}