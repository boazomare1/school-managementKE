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
public class AssignmentDto {

    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private Long teacherId;
    private String teacherName;
    private Long academicYearId;
    private String academicYearName;
    private Long termId;
    private String termName;
    private LocalDateTime assignedDate;
    private LocalDateTime dueDate;
    private BigDecimal totalMarks;
    private BigDecimal passingMarks;
    private String assignmentType;
    private String status;
    private String instructions;
    private String submissionFormat;
    private Integer maxAttempts;
    private Boolean allowLateSubmission;
    private Integer latePenaltyPercentage;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

