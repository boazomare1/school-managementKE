package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    
    private Long id;
    
    @NotBlank(message = "Report name is required")
    @Size(max = 100, message = "Report name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Report type is required")
    @Size(max = 50, message = "Report type must not exceed 50 characters")
    private String reportType;
    
    @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;
    
    @Size(max = 1000, message = "Parameters must not exceed 1000 characters")
    private String parameters;
    
    @Size(max = 500, message = "File path must not exceed 500 characters")
    private String filePath;
    
    @Size(max = 50, message = "File format must not exceed 50 characters")
    private String fileFormat;
    
    @NotNull(message = "Generated at is required")
    private LocalDateTime generatedAt;
    
    private Boolean isActive;
    
    private Long schoolId;
    private String schoolName;
    
    private Long generatedById;
    private String generatedByName;
}


