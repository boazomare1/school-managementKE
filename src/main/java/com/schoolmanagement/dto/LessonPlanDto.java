package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonPlanDto {

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
    private LocalDate lessonDate;
    private Integer duration;
    private String objectives;
    private String materials;
    private String activities;
    private String homework;
    private String assessment;
    private String notes;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

