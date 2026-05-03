package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder // Nesne oluştururken esneklik sağlar, profesyonel bir dokunuştur.
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse implements Serializable {

    // 🚀 Sektör standardı: Serileştirme sırasında sürüm çakışmalarını önler.
    private static final long serialVersionUID = 1L;

    private Long id;
    private String product;
    private Double price;
    private String status;
}