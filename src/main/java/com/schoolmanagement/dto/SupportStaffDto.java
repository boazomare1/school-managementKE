package com.schoolmanagement.dto;

import com.schoolmanagement.entity.SupportStaff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportStaffDto {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Employee ID is required")
    @Size(max = 50, message = "Employee ID must not exceed 50 characters")
    private String employeeId;
    
    @NotNull(message = "Staff type is required")
    private SupportStaff.SupportStaffType staffType;
    
    @NotNull(message = "Employment status is required")
    private SupportStaff.EmploymentStatus employmentStatus;
    
    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;
    
    private LocalDate terminationDate;
    
    @Size(max = 500, message = "Termination reason must not exceed 500 characters")
    private String terminationReason;
    
    @NotNull(message = "Basic salary is required")
    @Positive(message = "Basic salary must be positive")
    private BigDecimal basicSalary;
    
    private BigDecimal allowances;
    private BigDecimal deductions;
    
    @NotNull(message = "Net salary is required")
    @Positive(message = "Net salary must be positive")
    private BigDecimal netSalary;
    
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;
    
    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;
    
    @Size(max = 100, message = "Supervisor must not exceed 100 characters")
    private String supervisor;
    
    @Size(max = 50, message = "Work schedule must not exceed 50 characters")
    private String workSchedule;
    
    @Size(max = 20, message = "Employment type must not exceed 20 characters")
    private String employmentType;
    
    @Size(max = 50, message = "Bank name must not exceed 50 characters")
    private String bankName;
    
    @Size(max = 50, message = "Bank account must not exceed 50 characters")
    private String bankAccount;
    
    @Size(max = 20, message = "National ID must not exceed 20 characters")
    private String nationalId;
    
    @Size(max = 20, message = "Social security number must not exceed 20 characters")
    private String socialSecurityNumber;
    
    @Size(max = 20, message = "Tax PIN must not exceed 20 characters")
    private String taxPin;
    
    private Boolean isActive;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User details for response
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
}
