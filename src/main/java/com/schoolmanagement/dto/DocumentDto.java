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
public class DocumentDto {

    private Long id;
    private String fileName;
    private String originalFileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String documentType;
    private Long uploadedById;
    private String uploadedByName;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private Long academicYearId;
    private String academicYearName;
    private Long examId;
    private String examName;
    private Long assignmentId;
    private String assignmentTitle;
    private String title;
    private String description;
    private Boolean isPublic;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

