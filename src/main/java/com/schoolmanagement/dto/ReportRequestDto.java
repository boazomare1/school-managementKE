package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    
    @NotBlank(message = "Report name is required")
    @Size(max = 100, message = "Report name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Report type is required")
    @Size(max = 50, message = "Report type must not exceed 50 characters")
    private String reportType;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @Size(max = 50, message = "File format must not exceed 50 characters")
    private String fileFormat = "PDF";
    
    // Optional filters
    private Long classId;
    private Long subjectId;
    private Long studentId;
    private Long teacherId;
    
    @Size(max = 1000, message = "Additional parameters must not exceed 1000 characters")
    private String additionalParameters;
}


