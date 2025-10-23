package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    
    private Long id;
    
    @NotBlank(message = "Subject name is required")
    @Size(max = 100, message = "Subject name must not exceed 100 characters")
    private String name;
    
    @NotBlank(message = "Subject code is required")
    @Size(max = 20, message = "Subject code must not exceed 20 characters")
    private String code;
    
    @Size(max = 10, message = "Subject type must not exceed 10 characters")
    private String type;
    
    @NotNull(message = "Credits is required")
    @Positive(message = "Credits must be positive")
    private Integer credits;
    
    private Boolean isActive;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}


