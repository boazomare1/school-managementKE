package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto {

    private Long id;
    private String title;
    private String content;
    private String type;
    private String priority;
    private Long authorId;
    private String authorName;
    private Long schoolId;
    private String schoolName;
    private Long classId;
    private String className;
    private LocalDateTime publishDate;
    private LocalDateTime expiryDate;
    private Boolean isActive;
    private Boolean isPublished;
    private String attachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


