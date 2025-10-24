package com.schoolmanagement.dto;

import com.schoolmanagement.entity.Payroll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDto {
    
    private Long id;
    
    @NotNull(message = "Support staff ID is required")
    private Long supportStaffId;
    
    @NotNull(message = "Pay period start is required")
    private LocalDate payPeriodStart;
    
    @NotNull(message = "Pay period end is required")
    private LocalDate payPeriodEnd;
    
    @NotNull(message = "Pay date is required")
    private LocalDate payDate;
    
    @NotNull(message = "Basic salary is required")
    @Positive(message = "Basic salary must be positive")
    private BigDecimal basicSalary;
    
    private BigDecimal overtimePay;
    private BigDecimal totalAllowances;
    private BigDecimal bonuses;
    private BigDecimal grossPay;
    private BigDecimal taxDeduction;
    private BigDecimal socialSecurityDeduction;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductions;
    
    @NotNull(message = "Net pay is required")
    @Positive(message = "Net pay must be positive")
    private BigDecimal netPay;
    
    @NotNull(message = "Status is required")
    private Payroll.PayrollStatus status;
    
    private String paymentMethod;
    private String transactionReference;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Staff details for response
    private String employeeId;
    private String staffName;
    private String department;
    private String position;
    
    // Allowances and deductions
    private List<PayrollAllowanceDto> allowanceList;
    private List<PayrollDeductionDto> deductionList;
}