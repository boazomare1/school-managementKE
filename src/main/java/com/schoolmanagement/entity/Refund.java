package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String refundReference;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(nullable = false, length = 20)
    private String refundStatus; // PENDING, PROCESSED, FAILED, CANCELLED
    
    @Column(length = 20)
    private String refundMethod; // ORIGINAL_PAYMENT_METHOD, BANK_TRANSFER, CASH
    
    @Column(length = 100)
    private String transactionId;
    
    @Column(length = 500)
    private String refundReason;
    
    @Column(length = 500)
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime refundDate;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_id", nullable = false)
    private User processedBy;
}


