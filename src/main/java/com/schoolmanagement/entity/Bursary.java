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
@Table(name = "bursaries")
public class Bursary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(nullable = false)
    private String bursaryNumber;

    @Column(nullable = false)
    private String bursaryType; // GOVERNMENT, COUNTY, NGO, PRIVATE

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal approvedAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal disbursedAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal utilizedAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAmount;

    @Column(nullable = false)
    private LocalDate applicationDate;

    @Column(nullable = false)
    private LocalDate approvalDate;

    @Column(nullable = false)
    private LocalDate disbursementDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BursaryStatus status; // PENDING, APPROVED, DISBURSED, UTILIZED, COMPLETED

    @Column(length = 1000)
    private String applicationReason;

    @Column(length = 1000)
    private String approvalNotes;

    @Column(length = 1000)
    private String utilizationReport;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum BursaryStatus {
        PENDING,
        APPROVED,
        DISBURSED,
        UTILIZED,
        COMPLETED,
        CANCELLED
    }
}

