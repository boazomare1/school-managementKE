package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_deliveries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDelivery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryChannel channel;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;
    
    @Column(length = 1000)
    private String deliveryMessage;
    
    @Column(length = 500)
    private String externalId; // External service ID (e.g., email service ID)
    
    @Column(length = 1000)
    private String errorMessage;
    
    @Column
    private LocalDateTime sentAt;
    
    @Column
    private LocalDateTime deliveredAt;
    
    @Column
    private LocalDateTime failedAt;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;
    
    public enum DeliveryChannel {
        EMAIL,
        SMS,
        PUSH,
        IN_APP,
        WHATSAPP
    }
    
    public enum DeliveryStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED,
        BOUNCED
    }
}
