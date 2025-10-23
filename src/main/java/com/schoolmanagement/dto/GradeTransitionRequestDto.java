package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeTransitionRequestDto {

    @NotNull(message = "Student IDs are required")
    private List<Long> studentIds;

    @NotNull(message = "From Class ID is required")
    private Long fromClassId;

    @NotNull(message = "To Class ID is required")
    private Long toClassId;

    @NotNull(message = "Academic Year ID is required")
    private Long academicYearId;

    @NotNull(message = "Transition type is required")
    private String transitionType; // PROMOTE, REPEAT, TRANSFER

    private LocalDateTime transitionDate;

    private String notes;
}

