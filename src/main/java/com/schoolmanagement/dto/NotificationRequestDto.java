package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Type is required")
    private String type; // SYSTEM, EMAIL, SMS, PUSH, ANNOUNCEMENT

    @NotNull(message = "Priority is required")
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    private Long recipientId; // If null, send to all users in school/class

    private Long classId; // If specified, send to all users in this class

    private Long schoolId;

    private String metadata; // JSON string for additional data

    private LocalDateTime scheduledAt; // If null, send immediately

    private List<Long> recipientIds; // For bulk notifications
}


