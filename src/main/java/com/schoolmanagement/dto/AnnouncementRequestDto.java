package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Type is required")
    private String type; // GENERAL, CLASS_SPECIFIC, PARENT_ONLY, TEACHER_ONLY, STUDENT_ONLY

    @NotNull(message = "Priority is required")
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    private Long classId; // Required for CLASS_SPECIFIC type

    private Long schoolId;

    @NotNull(message = "Publish date is required")
    private LocalDateTime publishDate;

    private LocalDateTime expiryDate;

    private String attachments; // JSON string for file attachments
}


