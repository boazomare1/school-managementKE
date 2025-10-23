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
public class AcademicYearDto {
    
    private Long id;
    
    @NotBlank(message = "Academic year name is required")
    @Size(max = 50, message = "Academic year name must not exceed 50 characters")
    private String name;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    private Boolean isActive;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private Long schoolId;
    private String schoolName;
}


