package com.schoolmanagement.dto;

import com.schoolmanagement.entity.Bursary;
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
public class BursaryDto {
    
    private Long id;
    private Long studentId;
    private String studentName;
    private Long schoolId;
    private String schoolName;
    private Long academicYearId;
    private String academicYearName;
    private String bursaryNumber;
    private String bursaryType;
    private BigDecimal approvedAmount;
    private BigDecimal disbursedAmount;
    private BigDecimal utilizedAmount;
    private BigDecimal balanceAmount;
    private Bursary.BursaryStatus status;
    private String applicationReason;
    private String approvalNotes;
    private String utilizationReport;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

