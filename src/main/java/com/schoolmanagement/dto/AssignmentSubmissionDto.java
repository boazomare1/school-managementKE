package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmissionDto {

    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private LocalDateTime submittedAt;
    private String status;
    private String submissionText;
    private String filePath;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private BigDecimal score;
    private String grade;
    private String feedback;
    private Boolean isLate;
    private Integer attemptNumber;
    private Long gradedById;
    private String gradedByName;
    private LocalDateTime gradedAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

