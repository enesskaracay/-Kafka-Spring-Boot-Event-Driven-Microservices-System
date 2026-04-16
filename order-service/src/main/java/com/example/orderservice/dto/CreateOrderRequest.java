package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "Ürün adı boş olamaz veya sadece boşluktan oluşamaz!")
    private String product;

    @Positive(message = "Ürün fiyatı 0'dan büyük olmalıdır!")
    private Double price;
}