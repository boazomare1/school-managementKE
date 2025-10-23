package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonPlanRequestDto {

    @NotNull(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Academic Year ID is required")
    private Long academicYearId;

    private Long termId;

    @NotNull(message = "Lesson date is required")
    private LocalDate lessonDate;

    @NotNull(message = "Duration is required")
    private Integer duration;

    private String objectives;
    private String materials;
    private String activities;
    private String homework;
    private String assessment;
    private String notes;
}

