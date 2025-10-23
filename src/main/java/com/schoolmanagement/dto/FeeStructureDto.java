package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureDto {
    
    private Long id;
    
    @NotBlank(message = "Fee structure name is required")
    @Size(max = 100, message = "Fee structure name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Fee type is required")
    @Size(max = 20, message = "Fee type must not exceed 20 characters")
    private String feeType;
    
    @NotBlank(message = "Payment frequency is required")
    @Size(max = 20, message = "Payment frequency must not exceed 20 characters")
    private String paymentFrequency;
    
    private Boolean isMandatory;
    
    private Boolean isActive;
    
    @NotNull(message = "Effective from date is required")
    private LocalDateTime effectiveFrom;
    
    @NotNull(message = "Effective to date is required")
    private LocalDateTime effectiveTo;
    
    private Long schoolId;
    private String schoolName;
    
    private Long classId;
    private String className;
    
    private Long academicYearId;
    private String academicYearName;
}


