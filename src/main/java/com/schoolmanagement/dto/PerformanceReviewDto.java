package com.schoolmanagement.dto;

import com.schoolmanagement.entity.PerformanceReview;
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
public class PerformanceReviewDto {
    
    private Long id;
    private Long staffId;
    private String staffName;
    private Long reviewerId;
    private String reviewerName;
    private LocalDate reviewDate;
    private LocalDate reviewPeriodStart;
    private LocalDate reviewPeriodEnd;
    private String reviewType; // ANNUAL, MID_YEAR, PROBATIONARY, SPOT
    private PerformanceReview.ReviewStatus status;
    private BigDecimal overallRating;
    private BigDecimal teachingRating;
    private BigDecimal punctualityRating;
    private BigDecimal professionalismRating;
    private BigDecimal studentEngagementRating;
    private String strengths;
    private String areasForImprovement;
    private String goals;
    private String recommendations;
    private String comments;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
