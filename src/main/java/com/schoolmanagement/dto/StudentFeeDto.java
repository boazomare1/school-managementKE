package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long feeStructureId;
    private String feeStructureName;
    private Long classId;
    private String className;
    private Long academicYearId;
    private String academicYearName;
    private Long termId;
    private String termName;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String status;
    private LocalDateTime dueDate;
    private LocalDateTime paidDate;
    private String paymentReference;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

