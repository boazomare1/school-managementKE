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
public class StudentGradeDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long classId;
    private String className;
    private Long academicYearId;
    private String academicYearName;
    private String status;
    private LocalDateTime enrollmentDate;
    private LocalDateTime completionDate;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

