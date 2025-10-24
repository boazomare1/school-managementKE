package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "school_id")
    private Long schoolId;
    
    @Column(name = "class_id")
    private Long classId;
    
    @Column(length = 500)
    private String actionUrl; // URL to redirect when notification is clicked
    
    @Column(length = 100)
    private String actionText; // Text for action button
    
    @Column(length = 1000)
    private String metadata; // JSON string for additional data
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NotificationDelivery> deliveries;
    
    public enum NotificationType {
        WELCOME,
        ONBOARDING,
        PAYMENT,
        ASSIGNMENT,
        EXAM,
        ATTENDANCE,
        GRADE,
        FEE_REMINDER,
        SYSTEM_ALERT,
        TEACHER_ASSIGNMENT,
        CLASS_ASSIGNMENT,
        DORMITORY_ASSIGNMENT,
        LIBRARY_BORROW,
        LIBRARY_RETURN,
        STAFF_ONBOARDING,
        STAFF_TERMINATION,
        GENERAL
    }
    
    public enum NotificationPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
    
    public enum NotificationStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED,
        CANCELLED
    }
}