package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_gateway_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String gatewayName; // M_PESA, STRIPE, PAYPAL
    
    @Column(nullable = false, length = 20)
    private String environment; // SANDBOX, PRODUCTION
    
    @Column(nullable = false, length = 100)
    private String apiKey;
    
    @Column(length = 100)
    private String secretKey;
    
    @Column(length = 100)
    private String webhookSecret;
    
    @Column(length = 200)
    private String callbackUrl;
    
    @Column(length = 200)
    private String webhookUrl;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(length = 500)
    private String configuration;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
}


