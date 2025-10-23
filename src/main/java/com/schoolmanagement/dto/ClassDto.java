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
public class ClassDto {
    
    private Long id;
    
    @NotBlank(message = "Class name is required")
    @Size(max = 50, message = "Class name must not exceed 50 characters")
    private String name;
    
    @Size(max = 20, message = "Class code must not exceed 20 characters")
    private String code;
    
    @Size(max = 10, message = "Section must not exceed 10 characters")
    private String section;
    
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;
    
    private Boolean isActive;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private Long schoolId;
    private String schoolName;
    
    private Long academicYearId;
    private String academicYearName;
    
    private Long classTeacherId;
    private String classTeacherName;
}


