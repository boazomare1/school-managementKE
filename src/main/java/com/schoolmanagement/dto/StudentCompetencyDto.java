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
public class StudentCompetencyDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long competencyId;
    private String competencyName;
    private String competencyCode;
    private Long teacherId;
    private String teacherName;
    private Long academicYearId;
    private String academicYearName;
    private Long termId;
    private String termName;
    private String level;
    private String assessmentType;
    private String evidence;
    private String teacherComments;
    private String nextSteps;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

