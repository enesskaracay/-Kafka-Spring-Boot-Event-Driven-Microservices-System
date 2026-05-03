package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(CreateOrderRequest request);
    void updateOrderStatus(Long orderId, String status);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
}