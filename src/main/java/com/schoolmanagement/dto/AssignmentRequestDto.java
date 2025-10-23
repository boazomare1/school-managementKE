package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequestDto {

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

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    @NotNull(message = "Total marks is required")
    private BigDecimal totalMarks;

    @NotNull(message = "Passing marks is required")
    private BigDecimal passingMarks;

    @NotNull(message = "Assignment type is required")
    private String assignmentType;

    private String instructions;
    private String submissionFormat;
    private Integer maxAttempts = 1;
    private Boolean allowLateSubmission = false;
    private Integer latePenaltyPercentage = 0;
}

