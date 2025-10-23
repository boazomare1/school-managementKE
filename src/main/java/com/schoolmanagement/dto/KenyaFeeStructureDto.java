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
public class KenyaFeeStructureDto {

    private Long id;
    private Long schoolId;
    private String schoolName;
    private Long classId;
    private String className;
    private Long academicYearId;
    private String academicYearName;
    private String feeName;
    private String feeCode;
    private String feeType;
    private BigDecimal amount;
    private BigDecimal capitationAmount;
    private BigDecimal parentContribution;
    private String frequency;
    private Boolean isMandatory;
    private Boolean isCapitationEligible;
    private Boolean isBursaryEligible;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

