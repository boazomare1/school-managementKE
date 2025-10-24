package com.schoolmanagement.dto;

import com.schoolmanagement.entity.Teacher;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "TSC number is required")
    private String tscNumber;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotNull(message = "Teacher role is required")
    private Teacher.TeacherRole role;
    
    private Long assignedClassId;
    private Long assignedDormitoryId;
    
    private String qualifications;
    private String experience;
    private String notes;
    
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User information (for display)
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
}
