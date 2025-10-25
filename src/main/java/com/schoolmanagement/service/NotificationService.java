package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.NotificationDto;
import com.schoolmanagement.dto.NotificationRequestDto;
import com.schoolmanagement.dto.NotificationDeliveryDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final UserRepository userRepository;
    
    // Create a single notification
    public ApiResponse<NotificationDto> createNotification(NotificationRequestDto request) {
        try {
            log.info("Creating notification for recipient: {}", request.getRecipientId());
            
            User recipient = userRepository.findById(request.getRecipientId())
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));
            
            User sender = null;
            if (request.getSenderId() != null) {
                sender = userRepository.findById(request.getSenderId())
                        .orElse(null);
            }
            
            Notification notification = Notification.builder()
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .type(request.getType())
                    .priority(request.getPriority())
                    .priority(request.getPriority())
                    .status(Notification.NotificationStatus.PENDING)
                    .readAt(null)
                    .isActive(true)
                    .schoolId(1L) // Default school ID
                    .actionUrl(request.getActionUrl())
                    .actionText(request.getActionText())
                    .metadata(request.getMetadata())
                    .recipient(recipient)
                    .sender(sender)
                    .build();
            
            Notification savedNotification = notificationRepository.save(notification);
            
            // Send notification through configured channels
            sendNotification(savedNotification, request.getDeliveryChannels());
            
            log.info("Successfully created notification with ID: {}", savedNotification.getId());
            return ApiResponse.success("Notification created successfully", convertToDto(savedNotification));
            
        } catch (Exception e) {
            log.error("Error creating notification: {}", e.getMessage());
            return ApiResponse.error("Failed to create notification: " + e.getMessage());
        }
    }
    
    // Create bulk notifications
    public ApiResponse<List<NotificationDto>> createBulkNotifications(NotificationRequestDto request) {
        try {
            log.info("Creating bulk notifications for {} recipients", request.getRecipientIds().size());
            
            List<Notification> notifications = new ArrayList<>();
            
            for (Long recipientId : request.getRecipientIds()) {
                User recipient = userRepository.findById(recipientId)
                        .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientId));
                
                User sender = null;
                if (request.getSenderId() != null) {
                    sender = userRepository.findById(request.getSenderId())
                            .orElse(null);
                }
                
                Notification notification = Notification.builder()
                        .title(request.getTitle())
                        .message(request.getMessage())
                        .type(request.getType())
                        .priority(request.getPriority())
                        .status(Notification.NotificationStatus.PENDING)
                        .readAt(null)
                        .isActive(true)
                        .schoolId(1L) // Default school ID
                        .actionUrl(request.getActionUrl())
                        .actionText(request.getActionText())
                        .metadata(request.getMetadata())
                        .recipient(recipient)
                        .sender(sender)
                        .build();
                
                notifications.add(notification);
            }
            
            List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
            
            // Send notifications through configured channels
            for (Notification notification : savedNotifications) {
                sendNotification(notification, request.getDeliveryChannels());
            }
            
            log.info("Successfully created {} bulk notifications", savedNotifications.size());
            return ApiResponse.success("Bulk notifications created successfully", 
                    savedNotifications.stream().map(this::convertToDto).toList());
            
        } catch (Exception e) {
            log.error("Error creating bulk notifications: {}", e.getMessage());
            return ApiResponse.error("Failed to create bulk notifications: " + e.getMessage());
        }
    }
    
    // Create notification from template
    public ApiResponse<NotificationDto> createNotificationFromTemplate(String templateKey, Long recipientId, 
                                                                      Map<String, Object> variables, Long senderId) {
        try {
            log.info("Creating notification from template: {} for recipient: {}", templateKey, recipientId);
            
            NotificationTemplate template = templateRepository.findByTemplateKeyAndIsActiveTrue(templateKey)
                    .orElseThrow(() -> new RuntimeException("Template not found: " + templateKey));
            
            User recipient = userRepository.findById(recipientId)
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));
            
            User sender = null;
            if (senderId != null) {
                sender = userRepository.findById(senderId)
                        .orElse(null);
            }
            
            // Process template with variables
            String processedTitle = processTemplate(template.getTitle(), variables);
            String processedMessage = processTemplate(template.getMessage(), variables);
            String processedActionUrl = processTemplate(template.getActionUrl(), variables);
            
            Notification notification = Notification.builder()
                    .title(processedTitle)
                    .message(processedMessage)
                    .type(template.getType())
                    .priority(template.getPriority())
                    .status(Notification.NotificationStatus.PENDING)
                    .readAt(null)
                    .isActive(true)
                    .actionUrl(processedActionUrl)
                    .actionText(template.getActionText())
                    .recipient(recipient)
                    .sender(sender)
                    .build();
            
            Notification savedNotification = notificationRepository.save(notification);
            
            // Send notification
            sendNotification(savedNotification, null);
            
            log.info("Successfully created notification from template with ID: {}", savedNotification.getId());
            return ApiResponse.success("Notification created from template successfully", convertToDto(savedNotification));
            
        } catch (Exception e) {
            log.error("Error creating notification from template: {}", e.getMessage());
            return ApiResponse.error("Failed to create notification from template: " + e.getMessage());
        }
    }
    
    // Get notifications for a user
    @Transactional(readOnly = true)
    public ApiResponse<Page<NotificationDto>> getUserNotifications(Long userId, int page, int size, 
                                                                  String search, Notification.NotificationType type) {
        try {
            log.info("Fetching notifications for user: {}", userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Notification> notifications;
            
            if (type != null && search != null && !search.isEmpty()) {
                notifications = notificationRepository.findByRecipientAndTypeAndSearchTerm(user, type, search, pageable);
            } else if (type != null) {
                notifications = notificationRepository.findByRecipientAndTypeAndIsActiveTrueOrderByCreatedAtDesc(user, type, pageable);
            } else if (search != null && !search.isEmpty()) {
                notifications = notificationRepository.findByRecipientAndSearchTerm(user, search, pageable);
            } else {
                notifications = notificationRepository.findByRecipientAndIsActiveTrueOrderByCreatedAtDesc(user, pageable);
            }
            
            Page<NotificationDto> notificationDtos = notifications.map(this::convertToDto);
            return ApiResponse.success("Notifications retrieved successfully", notificationDtos);
            
        } catch (Exception e) {
            log.error("Error fetching user notifications: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch notifications: " + e.getMessage());
        }
    }
    
    // Get unread notifications count
    @Transactional(readOnly = true)
    public ApiResponse<Long> getUnreadCount(Long userId) {
        try {
            log.info("Fetching unread count for user: {}", userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Long count = notificationRepository.countByRecipientAndReadAtIsNullAndIsActiveTrue(user);
            return ApiResponse.success("Unread count retrieved successfully", count);
            
        } catch (Exception e) {
            log.error("Error fetching unread count: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch unread count: " + e.getMessage());
        }
    }
    
    // Mark notification as read
    public ApiResponse<NotificationDto> markAsRead(Long notificationId, Long userId) {
        try {
            log.info("Marking notification as read: {} for user: {}", notificationId, userId);
            
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));
            
            // Verify ownership
            if (!notification.getRecipient().getId().equals(userId)) {
                return ApiResponse.error("Unauthorized to mark this notification as read");
            }
            
            notification.setReadAt(LocalDateTime.now());
            notification.setReadAt(LocalDateTime.now());
            
            Notification updatedNotification = notificationRepository.save(notification);
            
            log.info("Successfully marked notification as read: {}", notificationId);
            return ApiResponse.success("Notification marked as read", convertToDto(updatedNotification));
            
        } catch (Exception e) {
            log.error("Error marking notification as read: {}", e.getMessage());
            return ApiResponse.error("Failed to mark notification as read: " + e.getMessage());
        }
    }
    
    // Mark all notifications as read for a user
    public ApiResponse<String> markAllAsRead(Long userId) {
        try {
            log.info("Marking all notifications as read for user: {}", userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Notification> unreadNotifications = notificationRepository
                    .findByRecipientAndReadAtIsNullAndIsActiveTrueOrderByCreatedAtDesc(user);
            
            for (Notification notification : unreadNotifications) {
                notification.setReadAt(LocalDateTime.now());
                notification.setReadAt(LocalDateTime.now());
            }
            
            notificationRepository.saveAll(unreadNotifications);
            
            log.info("Successfully marked {} notifications as read for user: {}", 
                    unreadNotifications.size(), userId);
            return ApiResponse.success("All notifications marked as read");
            
        } catch (Exception e) {
            log.error("Error marking all notifications as read: {}", e.getMessage());
            return ApiResponse.error("Failed to mark all notifications as read: " + e.getMessage());
        }
    }
    
    // Delete notification
    public ApiResponse<String> deleteNotification(Long notificationId, Long userId) {
        try {
            log.info("Deleting notification: {} for user: {}", notificationId, userId);
            
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));
            
            // Verify ownership
            if (!notification.getRecipient().getId().equals(userId)) {
                return ApiResponse.error("Unauthorized to delete this notification");
            }
            
            notification.setIsActive(false);
            notificationRepository.save(notification);
            
            log.info("Successfully deleted notification: {}", notificationId);
            return ApiResponse.success("Notification deleted successfully");
            
        } catch (Exception e) {
            log.error("Error deleting notification: {}", e.getMessage());
            return ApiResponse.error("Failed to delete notification: " + e.getMessage());
        }
    }
    
    // Send notification through configured channels
    private void sendNotification(Notification notification, List<NotificationDeliveryDto> deliveryChannels) {
        try {
            log.info("Sending notification: {} through channels", notification.getId());
            
            // Default delivery channels if none specified
            if (deliveryChannels == null || deliveryChannels.isEmpty()) {
                deliveryChannels = getDefaultDeliveryChannels(notification.getRecipient());
            }
            
            for (NotificationDeliveryDto deliveryDto : deliveryChannels) {
                try {
                    NotificationDelivery delivery = NotificationDelivery.builder()
                            .channel(deliveryDto.getChannel())
                            .status(NotificationDelivery.DeliveryStatus.PENDING)
                            .deliveryMessage(deliveryDto.getDeliveryMessage())
                            .notification(notification)
                            .build();
                    
                    // Send through appropriate channel
                    boolean sent = sendThroughChannel(notification, delivery, deliveryDto);
                    
                    if (sent) {
                        delivery.setStatus(NotificationDelivery.DeliveryStatus.SENT);
                        delivery.setSentAt(LocalDateTime.now());
                    } else {
                        delivery.setStatus(NotificationDelivery.DeliveryStatus.FAILED);
                        delivery.setFailedAt(LocalDateTime.now());
                    }
                    
                } catch (Exception e) {
                    log.error("Error sending notification through channel {}: {}", 
                            deliveryDto.getChannel(), e.getMessage());
                }
            }
            
            // Update notification status
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }
    
    // Get default delivery channels for a user
    private List<NotificationDeliveryDto> getDefaultDeliveryChannels(User user) {
        List<NotificationDeliveryDto> channels = new ArrayList<>();
        
        // Always add in-app notification
        channels.add(NotificationDeliveryDto.builder()
                .channel(NotificationDelivery.DeliveryChannel.IN_APP)
                .build());
        
        // Add email if user has email
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            channels.add(NotificationDeliveryDto.builder()
                    .channel(NotificationDelivery.DeliveryChannel.EMAIL)
                    .build());
        }
        
        // Add SMS if user has phone number
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            channels.add(NotificationDeliveryDto.builder()
                    .channel(NotificationDelivery.DeliveryChannel.SMS)
                    .build());
        }
        
        return channels;
    }
    
    // Send notification through specific channel
    private boolean sendThroughChannel(Notification notification, NotificationDelivery delivery, 
                                     NotificationDeliveryDto deliveryDto) {
        try {
            switch (delivery.getChannel()) {
                case EMAIL:
                    return sendEmailNotification(notification, delivery);
                case SMS:
                    return sendSmsNotification(notification, delivery);
                case PUSH:
                    return sendPushNotification(notification, delivery);
                case IN_APP:
                    return true; // In-app notifications are always "sent"
                case WHATSAPP:
                    return sendWhatsAppNotification(notification, delivery);
                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Error sending notification through channel {}: {}", 
                    delivery.getChannel(), e.getMessage());
            return false;
        }
    }
    
    // Email notification (placeholder - would integrate with email service)
    private boolean sendEmailNotification(Notification notification, NotificationDelivery delivery) {
        log.info("Sending email notification to: {}", notification.getRecipient().getEmail());
        // TODO: Integrate with email service
        return true;
    }
    
    // SMS notification (placeholder - would integrate with SMS service)
    private boolean sendSmsNotification(Notification notification, NotificationDelivery delivery) {
        log.info("Sending SMS notification to: {}", notification.getRecipient().getPhoneNumber());
        // TODO: Integrate with SMS service
        return true;
    }
    
    // Push notification (placeholder - would integrate with push service)
    private boolean sendPushNotification(Notification notification, NotificationDelivery delivery) {
        log.info("Sending push notification to user: {}", notification.getRecipient().getId());
        // TODO: Integrate with push notification service
        return true;
    }
    
    // WhatsApp notification (placeholder - would integrate with WhatsApp service)
    private boolean sendWhatsAppNotification(Notification notification, NotificationDelivery delivery) {
        log.info("Sending WhatsApp notification to: {}", notification.getRecipient().getPhoneNumber());
        // TODO: Integrate with WhatsApp service
        return true;
    }
    
    // Process template with variables
    private String processTemplate(String template, Map<String, Object> variables) {
        if (template == null || variables == null) {
            return template;
        }
        
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }
        
        return result;
    }
    
    // Convert entity to DTO
    private NotificationDto convertToDto(Notification notification) {
        try {
            return NotificationDto.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .type(notification.getType())
                    .priority(notification.getPriority())
                    .status(notification.getStatus())
                    .readAt(notification.getReadAt())
                    .isActive(notification.getIsActive())
                    .actionUrl(notification.getActionUrl())
                    .actionText(notification.getActionText())
                    .metadata(notification.getMetadata())
                    .createdAt(notification.getCreatedAt())
                    .updatedAt(notification.getUpdatedAt())
                    .readAt(notification.getReadAt())
                    .sentAt(notification.getSentAt())
                    .recipientId(notification.getRecipient() != null ? notification.getRecipient().getId() : null)
                    .recipientName(notification.getRecipient() != null ? 
                            notification.getRecipient().getFirstName() + " " + notification.getRecipient().getLastName() : null)
                    .recipientEmail(notification.getRecipient() != null ? notification.getRecipient().getEmail() : null)
                    .senderId(notification.getSender() != null ? notification.getSender().getId() : null)
                    .senderName(notification.getSender() != null ? 
                            notification.getSender().getFirstName() + " " + notification.getSender().getLastName() : null)
                    .senderEmail(notification.getSender() != null ? notification.getSender().getEmail() : null)
                    .build();
        } catch (Exception e) {
            log.error("Error converting notification to DTO: {}", e.getMessage());
            return NotificationDto.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .type(notification.getType())
                    .priority(notification.getPriority())
                    .status(notification.getStatus())
                    .readAt(notification.getReadAt())
                    .isActive(notification.getIsActive())
                    .createdAt(notification.getCreatedAt())
                    .build();
        }
    }
}
