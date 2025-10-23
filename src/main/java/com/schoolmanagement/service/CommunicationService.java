package com.schoolmanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.AnnouncementDto;
import com.schoolmanagement.dto.AnnouncementRequestDto;
import com.schoolmanagement.dto.EmailTemplateDto;
import com.schoolmanagement.dto.NotificationDto;
import com.schoolmanagement.dto.NotificationRequestDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunicationService {

    private final NotificationRepository notificationRepository;
    private final AnnouncementRepository announcementRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailLogRepository emailLogRepository;
    private final SmsLogRepository smsLogRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final ObjectMapper objectMapper;

    // Notification Management
    public ApiResponse<NotificationDto> sendNotification(NotificationRequestDto request, User sender) {
        try {
            log.info("Sending notification: {} to user: {}", request.getTitle(), request.getRecipientId());

            // Get school from sender
            School school = schoolRepository.findById(request.getSchoolId()).orElse(null);
            if (school == null) {
                return ApiResponse.error("School not found");
            }

            Notification notification = Notification.builder()
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .type(Notification.NotificationType.valueOf(request.getType()))
                    .priority(Notification.NotificationPriority.valueOf(request.getPriority()))
                    .status(Notification.NotificationStatus.DRAFT)
                    .sender(sender)
                    .school(school)
                    .metadata(request.getMetadata())
                    .scheduledAt(request.getScheduledAt() != null ? request.getScheduledAt() : LocalDateTime.now())
                    .isActive(true)
                    .build();

            // Set recipient if specified
            if (request.getRecipientId() != null) {
                User recipient = userRepository.findById(request.getRecipientId()).orElse(null);
                if (recipient != null) {
                    notification.setRecipient(recipient);
                }
            }

            // Set class if specified
            if (request.getClassId() != null) {
                ClassEntity classEntity = classRepository.findById(request.getClassId()).orElse(null);
                if (classEntity != null) {
                    notification.setClassEntity(classEntity);
                }
            }

            Notification savedNotification = notificationRepository.save(notification);

            // Mark as sent if scheduled for now
            if (notification.getScheduledAt().isBefore(LocalDateTime.now()) || notification.getScheduledAt().isEqual(LocalDateTime.now())) {
                savedNotification.setStatus(Notification.NotificationStatus.SENT);
                savedNotification.setSentAt(LocalDateTime.now());
                notificationRepository.save(savedNotification);
            }

            return ApiResponse.success("Notification sent successfully", convertToDto(savedNotification));

        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            return ApiResponse.error("Failed to send notification: " + e.getMessage());
        }
    }

    public ApiResponse<List<NotificationDto>> getNotificationsByUser(Long userId) {
        try {
            log.info("Fetching notifications for user: {}", userId);
            List<Notification> notifications = notificationRepository.findByRecipientIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
            List<NotificationDto> notificationDtos = notifications.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ApiResponse.success("Notifications retrieved successfully", notificationDtos);
        } catch (Exception e) {
            log.error("Error fetching notifications: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve notifications: " + e.getMessage());
        }
    }

    public ApiResponse<NotificationDto> markAsRead(Long notificationId) {
        try {
            log.info("Marking notification as read: {}", notificationId);
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification == null) {
                return ApiResponse.error("Notification not found");
            }

            notification.setStatus(Notification.NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);

            return ApiResponse.success("Notification marked as read", convertToDto(notification));
        } catch (Exception e) {
            log.error("Error marking notification as read: {}", e.getMessage());
            return ApiResponse.error("Failed to mark notification as read: " + e.getMessage());
        }
    }

    // Announcement Management
    public ApiResponse<AnnouncementDto> createAnnouncement(AnnouncementRequestDto request, User author) {
        try {
            log.info("Creating announcement: {} by user: {}", request.getTitle(), author.getUsername());

            School school = schoolRepository.findById(request.getSchoolId()).orElse(null);
            if (school == null) {
                return ApiResponse.error("School not found");
            }

            Announcement announcement = Announcement.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(Announcement.AnnouncementType.valueOf(request.getType()))
                    .priority(Announcement.AnnouncementPriority.valueOf(request.getPriority()))
                    .author(author)
                    .school(school)
                    .publishDate(request.getPublishDate())
                    .expiryDate(request.getExpiryDate())
                    .attachments(request.getAttachments())
                    .isActive(true)
                    .isPublished(false)
                    .build();

            // Set class if specified
            if (request.getClassId() != null) {
                ClassEntity classEntity = classRepository.findById(request.getClassId()).orElse(null);
                if (classEntity != null) {
                    announcement.setClassEntity(classEntity);
                }
            }

            Announcement savedAnnouncement = announcementRepository.save(announcement);
            return ApiResponse.success("Announcement created successfully", convertToDto(savedAnnouncement));

        } catch (Exception e) {
            log.error("Error creating announcement: {}", e.getMessage());
            return ApiResponse.error("Failed to create announcement: " + e.getMessage());
        }
    }

    public ApiResponse<AnnouncementDto> publishAnnouncement(Long announcementId) {
        try {
            log.info("Publishing announcement: {}", announcementId);
            Announcement announcement = announcementRepository.findById(announcementId).orElse(null);
            if (announcement == null) {
                return ApiResponse.error("Announcement not found");
            }

            announcement.setIsPublished(true);
            announcementRepository.save(announcement);

            return ApiResponse.success("Announcement published successfully", convertToDto(announcement));
        } catch (Exception e) {
            log.error("Error publishing announcement: {}", e.getMessage());
            return ApiResponse.error("Failed to publish announcement: " + e.getMessage());
        }
    }

    public ApiResponse<List<AnnouncementDto>> getActiveAnnouncements(Long schoolId) {
        try {
            log.info("Fetching active announcements for school: {}", schoolId);
            List<Announcement> announcements = announcementRepository.findActiveBySchoolId(schoolId, LocalDateTime.now());
            List<AnnouncementDto> announcementDtos = announcements.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ApiResponse.success("Active announcements retrieved successfully", announcementDtos);
        } catch (Exception e) {
            log.error("Error fetching announcements: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve announcements: " + e.getMessage());
        }
    }

    // Email Template Management
    public ApiResponse<EmailTemplateDto> createEmailTemplate(EmailTemplateDto templateDto) {
        try {
            log.info("Creating email template: {}", templateDto.getName());

            School school = schoolRepository.findById(templateDto.getSchoolId()).orElse(null);
            if (school == null) {
                return ApiResponse.error("School not found");
            }

            EmailTemplate template = EmailTemplate.builder()
                    .name(templateDto.getName())
                    .subject(templateDto.getSubject())
                    .body(templateDto.getBody())
                    .type(EmailTemplate.EmailType.valueOf(templateDto.getType()))
                    .school(school)
                    .variables(templateDto.getVariables())
                    .isActive(true)
                    .isDefault(templateDto.getIsDefault())
                    .build();

            EmailTemplate savedTemplate = emailTemplateRepository.save(template);
            return ApiResponse.success("Email template created successfully", convertToDto(savedTemplate));

        } catch (Exception e) {
            log.error("Error creating email template: {}", e.getMessage());
            return ApiResponse.error("Failed to create email template: " + e.getMessage());
        }
    }

    public ApiResponse<List<EmailTemplateDto>> getEmailTemplatesBySchool(Long schoolId) {
        try {
            log.info("Fetching email templates for school: {}", schoolId);
            List<EmailTemplate> templates = emailTemplateRepository.findBySchoolIdAndIsActiveTrueOrderByName(schoolId);
            List<EmailTemplateDto> templateDtos = templates.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ApiResponse.success("Email templates retrieved successfully", templateDtos);
        } catch (Exception e) {
            log.error("Error fetching email templates: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve email templates: " + e.getMessage());
        }
    }

    // Helper methods for conversion
    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType().name())
                .priority(notification.getPriority().name())
                .status(notification.getStatus().name())
                .senderId(notification.getSender().getId())
                .senderName(notification.getSender().getFirstName() + " " + notification.getSender().getLastName())
                .recipientId(notification.getRecipient() != null ? notification.getRecipient().getId() : null)
                .recipientName(notification.getRecipient() != null ? 
                    notification.getRecipient().getFirstName() + " " + notification.getRecipient().getLastName() : null)
                .schoolId(notification.getSchool().getId())
                .schoolName(notification.getSchool().getName())
                .classId(notification.getClassEntity() != null ? notification.getClassEntity().getId() : null)
                .className(notification.getClassEntity() != null ? notification.getClassEntity().getName() : null)
                .metadata(notification.getMetadata())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .isActive(notification.getIsActive())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    private AnnouncementDto convertToDto(Announcement announcement) {
        return AnnouncementDto.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .type(announcement.getType().name())
                .priority(announcement.getPriority().name())
                .authorId(announcement.getAuthor().getId())
                .authorName(announcement.getAuthor().getFirstName() + " " + announcement.getAuthor().getLastName())
                .schoolId(announcement.getSchool().getId())
                .schoolName(announcement.getSchool().getName())
                .classId(announcement.getClassEntity() != null ? announcement.getClassEntity().getId() : null)
                .className(announcement.getClassEntity() != null ? announcement.getClassEntity().getName() : null)
                .publishDate(announcement.getPublishDate())
                .expiryDate(announcement.getExpiryDate())
                .isActive(announcement.getIsActive())
                .isPublished(announcement.getIsPublished())
                .attachments(announcement.getAttachments())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }

    private EmailTemplateDto convertToDto(EmailTemplate template) {
        return EmailTemplateDto.builder()
                .id(template.getId())
                .name(template.getName())
                .subject(template.getSubject())
                .body(template.getBody())
                .type(template.getType().name())
                .schoolId(template.getSchool().getId())
                .schoolName(template.getSchool().getName())
                .variables(template.getVariables())
                .isActive(template.getIsActive())
                .isDefault(template.getIsDefault())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}


