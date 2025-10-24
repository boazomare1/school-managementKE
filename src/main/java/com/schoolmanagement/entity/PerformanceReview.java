package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "performance_reviews")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_staff_id", nullable = false)
    private SupportStaff supportStaff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(nullable = false)
    private LocalDate reviewPeriodStart;

    @Column(nullable = false)
    private LocalDate reviewPeriodEnd;

    @Column(nullable = false)
    private LocalDate reviewDate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal overallRating; // 1.0 to 5.0

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal teachingRating;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal punctualityRating;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal professionalismRating;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal studentEngagementRating;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String areasForImprovement;

    @Column(columnDefinition = "TEXT")
    private String goals;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status; // DRAFT, COMPLETED, APPROVED

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ReviewStatus {
        DRAFT,
        COMPLETED,
        APPROVED
    }
}

