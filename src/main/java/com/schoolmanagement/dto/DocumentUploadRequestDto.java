package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadRequestDto {

    @NotNull(message = "Document type is required")
    private String documentType;

    private Long subjectId;
    private Long classId;
    private Long academicYearId;
    private Long examId;
    private Long assignmentId;
    private String title;
    private String description;
    private Boolean isPublic = false;
}

