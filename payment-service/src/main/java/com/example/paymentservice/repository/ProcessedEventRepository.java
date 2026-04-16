package com.example.paymentservice.repository;

import com.example.paymentservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    // Veritabanında bu sipariş ID'si var mı diye anında kontrol eden sihirli metot
    boolean existsByOrderId(Long orderId);
}