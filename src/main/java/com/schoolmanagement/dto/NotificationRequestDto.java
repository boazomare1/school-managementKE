package com.schoolmanagement.dto;

import com.schoolmanagement.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    
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
    
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;
    
    private Long senderId;
    private String actionUrl;
    private String actionText;
    private String metadata;
    
    // For bulk notifications
    private List<Long> recipientIds;
    
    // For template-based notifications
    private String templateKey;
    private Map<String, Object> templateVariables;
    
    // Delivery preferences
    private List<NotificationDeliveryDto> deliveryChannels;
}