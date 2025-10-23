package com.schoolmanagement.dto;

import com.schoolmanagement.entity.AdmissionApplication;
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
public class AdmissionApplicationDto {
    
    private Long id;
    private String applicationNumber;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String idNumber;
    private String phoneNumber;
    private String email;
    private String address;
    private String previousSchool;
    private String previousSchoolLocation;
    private String previousClass;
    private String kcpeIndex;
    private BigDecimal kcpeMarks;
    private String kcpeYear;
    private String parentName;
    private String parentPhone;
    private String parentEmail;
    private String parentOccupation;
    private String parentAddress;
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private String guardianRelationship;
    private Long applyingClassId;
    private String applyingClassName;
    private Long academicYearId;
    private String academicYearName;
    private AdmissionApplication.ApplicationStatus status;
    private LocalDate applicationDate;
    private LocalDate reviewDate;
    private LocalDate decisionDate;
    private String reviewNotes;
    private String decisionReason;
    private BigDecimal applicationFee;
    private Boolean applicationFeePaid;
    private Boolean isActive;
    private Long reviewedById;
    private String reviewedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

