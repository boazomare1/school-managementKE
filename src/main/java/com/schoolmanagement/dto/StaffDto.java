package com.schoolmanagement.dto;

import com.schoolmanagement.entity.Staff;
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
public class StaffDto {
    
    private Long id;
    private Long userId;
    private String employeeNumber;
    private String tscNumber;
    private Staff.StaffType staffType;
    private Staff.EmploymentType employmentType;
    private LocalDate employmentDate;
    private LocalDate terminationDate;
    private BigDecimal basicSalary;
    private BigDecimal houseAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal medicalAllowance;
    private BigDecimal otherAllowances;
    private BigDecimal nhifDeduction;
    private BigDecimal nssfDeduction;
    private BigDecimal payeDeduction;
    private BigDecimal otherDeductions;
    private Boolean isActive;
    private String qualifications;
    private String certifications;
    private String experience;
    private String bankName;
    private String bankAccount;
    private String nhifNumber;
    private String nssfNumber;
    private String kraPin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User details
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
}

