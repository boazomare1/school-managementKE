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
public class CbcCompetencyDto {

    private Long id;
    private String competencyCode;
    private String competencyName;
    private String description;
    private String gradeLevel;
    private String learningArea;
    private String strand;
    private String subStrand;
    private Integer sequence;
    private Boolean isCoreCompetency;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

