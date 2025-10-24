package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.NotificationDto;
import com.schoolmanagement.dto.NotificationRequestDto;
import com.schoolmanagement.entity.Notification;
import com.schoolmanagement.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    // Create a single notification
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<NotificationDto>> createNotification(@Valid @RequestBody NotificationRequestDto request) {
        log.info("Creating notification request: {}", request.getTitle());
        return ResponseEntity.ok(notificationService.createNotification(request));
    }
    
    // Create bulk notifications
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> createBulkNotifications(@Valid @RequestBody NotificationRequestDto request) {
        log.info("Creating bulk notifications request: {}", request.getTitle());
        return ResponseEntity.ok(notificationService.createBulkNotifications(request));
    }
    
    // Create notification from template
    @PostMapping("/template/{templateKey}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<NotificationDto>> createNotificationFromTemplate(
            @PathVariable String templateKey,
            @RequestParam Long recipientId,
            @RequestBody(required = false) Map<String, Object> variables,
            @RequestParam(required = false) Long senderId) {
        log.info("Creating notification from template: {} for recipient: {}", templateKey, recipientId);
        return ResponseEntity.ok(notificationService.createNotificationFromTemplate(templateKey, recipientId, variables, senderId));
    }
    
    // Get current user's notifications
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Notification.NotificationType type,
            Authentication authentication) {
        log.info("Get user notifications request - page: {}, size: {}, search: {}, type: {}", page, size, search, type);
        
        // Get current user ID from authentication
        Long userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, page, size, search, type));
    }
    
    // Get unread notifications count
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        log.info("Get unread count request");
        
        Long userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }
    
    // Mark notification as read
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable Long notificationId, Authentication authentication) {
        log.info("Mark notification as read request: {}", notificationId);
        
        Long userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, userId));
    }
    
    // Mark all notifications as read
    @PutMapping("/mark-all-read")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(Authentication authentication) {
        log.info("Mark all notifications as read request");
        
        Long userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.markAllAsRead(userId));
    }
    
    // Delete notification
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Long notificationId, Authentication authentication) {
        log.info("Delete notification request: {}", notificationId);
        
        Long userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.deleteNotification(notificationId, userId));
    }
    
    // Helper method to get current user ID
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        // Assuming the principal is a User entity
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.schoolmanagement.entity.User) {
            return ((com.schoolmanagement.entity.User) principal).getId();
        }
        
        throw new RuntimeException("Unable to get current user ID");
    }
}
