package com.example.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "orders")
@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Sınıfta olmayan alan gelirse hata verme, görmezden gel
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("orderId") // JSON'daki "orderId" alanını buradaki "id" ile eşleştirir
    private Long id;

    private String product;
    private Double price;

    // 🚀 DEĞİŞEN KISIM: Artık rastgele String değil, sadece Enum'daki 3 değeri alabilir.
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private Instant createdAt = Instant.now();
}