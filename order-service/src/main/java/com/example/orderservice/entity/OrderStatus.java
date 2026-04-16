package com.example.orderservice.entity;

// Bu sadece siparişin alabileceği 3 kesin durumu belirten bir kalıptır.
public enum OrderStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}