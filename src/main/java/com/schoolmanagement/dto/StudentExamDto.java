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
public class StudentExamDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long examId;
    private String examName;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private String status;
    private Double score;
    private String grade;
    private String remarks;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime submittedAt;
    private Long gradedById;
    private String gradedByName;
    private LocalDateTime gradedAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

