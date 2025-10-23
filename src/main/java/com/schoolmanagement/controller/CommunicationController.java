package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.AnnouncementDto;
import com.schoolmanagement.dto.AnnouncementRequestDto;
import com.schoolmanagement.dto.EmailTemplateDto;
import com.schoolmanagement.dto.NotificationDto;
import com.schoolmanagement.dto.NotificationRequestDto;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.service.CommunicationService;
import com.schoolmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
@Slf4j
public class CommunicationController {

    private final CommunicationService communicationService;
    private final UserRepository userRepository;

    // Notification endpoints
    @PostMapping("/notifications/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<NotificationDto>> sendNotification(@Valid @RequestBody NotificationRequestDto request, Authentication authentication) {
        log.info("Send notification request: {}", request.getTitle());
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ApiResponse<NotificationDto> response = communicationService.sendNotification(request, currentUser);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/notifications/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotificationsByUser(@PathVariable Long userId) {
        log.info("Get notifications request for user: {}", userId);
        ApiResponse<List<NotificationDto>> response = communicationService.getNotificationsByUser(userId);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<NotificationDto>> markNotificationAsRead(@PathVariable Long notificationId) {
        log.info("Mark notification as read: {}", notificationId);
        ApiResponse<NotificationDto> response = communicationService.markAsRead(notificationId);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    // Announcement endpoints
    @PostMapping("/announcements")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AnnouncementDto>> createAnnouncement(@Valid @RequestBody AnnouncementRequestDto request, Authentication authentication) {
        log.info("Create announcement request: {}", request.getTitle());
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ApiResponse<AnnouncementDto> response = communicationService.createAnnouncement(request, currentUser);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @PutMapping("/announcements/{announcementId}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AnnouncementDto>> publishAnnouncement(@PathVariable Long announcementId) {
        log.info("Publish announcement: {}", announcementId);
        ApiResponse<AnnouncementDto> response = communicationService.publishAnnouncement(announcementId);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/announcements/school/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getActiveAnnouncements(@PathVariable Long schoolId) {
        log.info("Get active announcements for school: {}", schoolId);
        ApiResponse<List<AnnouncementDto>> response = communicationService.getActiveAnnouncements(schoolId);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    // Email Template endpoints
    @PostMapping("/email-templates")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmailTemplateDto>> createEmailTemplate(@Valid @RequestBody EmailTemplateDto templateDto) {
        log.info("Create email template: {}", templateDto.getName());
        ApiResponse<EmailTemplateDto> response = communicationService.createEmailTemplate(templateDto);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/email-templates/school/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<EmailTemplateDto>>> getEmailTemplatesBySchool(@PathVariable Long schoolId) {
        log.info("Get email templates for school: {}", schoolId);
        ApiResponse<List<EmailTemplateDto>> response = communicationService.getEmailTemplatesBySchool(schoolId);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    // Dashboard/Stats endpoints
    @GetMapping("/stats/notifications/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Object>> getNotificationStats(@PathVariable Long userId) {
        log.info("Get notification stats for user: {}", userId);
        // This would return unread count, total notifications, etc.
        return ResponseEntity.ok(ApiResponse.success("Notification stats (placeholder)", 
            java.util.Map.of("unreadCount", 5, "totalNotifications", 25)));
    }

    @GetMapping("/stats/announcements/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Object>> getAnnouncementStats(@PathVariable Long schoolId) {
        log.info("Get announcement stats for school: {}", schoolId);
        // This would return total announcements, published, drafts, etc.
        return ResponseEntity.ok(ApiResponse.success("Announcement stats (placeholder)", 
            java.util.Map.of("totalAnnouncements", 15, "published", 12, "drafts", 3)));
    }
}
