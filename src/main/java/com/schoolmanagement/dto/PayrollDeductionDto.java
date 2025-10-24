package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDeductionDto {
    
    private Long id;
    
    @NotNull(message = "Payroll ID is required")
    private Long payrollId;
    
    @NotBlank(message = "Deduction type is required")
    private String deductionType;
    
    private String description;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Is mandatory is required")
    private Boolean isMandatory;
    
    private String reference;
    private LocalDateTime createdAt;
}
