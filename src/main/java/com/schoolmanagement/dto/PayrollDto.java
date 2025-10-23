package com.schoolmanagement.dto;

import com.schoolmanagement.entity.Payroll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDto {
    
    private Long id;
    private Long staffId;
    private String staffName;
    private String employeeNumber;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private LocalDate payDate;
    private BigDecimal basicSalary;
    private BigDecimal houseAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal medicalAllowance;
    private BigDecimal otherAllowances;
    private BigDecimal grossSalary;
    private BigDecimal nhifDeduction;
    private BigDecimal nssfDeduction;
    private BigDecimal payeDeduction;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
    private Payroll.PayrollStatus status;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

