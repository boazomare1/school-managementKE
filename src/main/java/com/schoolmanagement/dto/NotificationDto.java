package com.schoolmanagement.dto;

import com.schoolmanagement.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;
    
    @NotNull(message = "Notification type is required")
    private Notification.NotificationType type;
    
    @NotNull(message = "Priority is required")
    private Notification.NotificationPriority priority;
    
    private Notification.NotificationStatus status;
    private Boolean isRead;
    private Boolean isActive;
    private String actionUrl;
    private String actionText;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readAt;
    private LocalDateTime sentAt;
    
    // User details
    private Long recipientId;
    private String recipientName;
    private String recipientEmail;
    
    private Long senderId;
    private String senderName;
    private String senderEmail;
}