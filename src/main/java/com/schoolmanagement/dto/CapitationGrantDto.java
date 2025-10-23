package com.schoolmanagement.dto;

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
public class CapitationGrantDto {

    private Long id;
    private Long schoolId;
    private String schoolName;
    private Long academicYearId;
    private String academicYearName;
    private String grantNumber;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private BigDecimal pendingAmount;
    private String status;
    private LocalDate expectedDate;
    private LocalDate receivedDate;
    private Integer studentCount;
    private BigDecimal perStudentAmount;
    private String remarks;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
