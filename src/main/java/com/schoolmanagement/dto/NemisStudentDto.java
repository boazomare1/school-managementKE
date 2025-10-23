package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NemisStudentDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private String nemisNumber;
    private String upiNumber;
    private String birthCertificateNumber;
    private String nationalIdNumber;
    private String county;
    private String subCounty;
    private String ward;
    private String constituency;
    private String disabilityStatus;
    private String disabilityDescription;
    private String orphanStatus;
    private String vulnerableStatus;
    private Boolean isSpecialNeeds;
    private String specialNeedsDescription;
    private Boolean isOrphan;
    private Boolean isVulnerable;
    private Boolean isBursaryRecipient;
    private String bursarySource;
    private Boolean isCapitationRecipient;
    private Boolean isNemisSynced;
    private LocalDateTime lastNemisSync;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

