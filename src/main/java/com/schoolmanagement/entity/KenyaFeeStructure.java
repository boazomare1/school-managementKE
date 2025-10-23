package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kenya_fee_structures")
public class KenyaFeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(nullable = false)
    private String feeName;

    @Column(nullable = false)
    private String feeCode; // e.g., "TUITION", "LIBRARY", "MEDICAL"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal capitationAmount = BigDecimal.ZERO; // Government capitation

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal parentContribution = BigDecimal.ZERO; // Parent contribution

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentFrequency frequency;

    @Column(nullable = false)
    private Boolean isMandatory = true;

    @Column(nullable = false)
    private Boolean isCapitationEligible = true; // Eligible for government capitation

    @Column(nullable = false)
    private Boolean isBursaryEligible = false; // Eligible for bursary support

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum FeeType {
        TUITION,
        LIBRARY,
        MEDICAL,
        TRANSPORT,
        BOARDING,
        EXAMINATION,
        DEVELOPMENT,
        SPORTS,
        COMPUTER,
        LABORATORY,
        TEXTBOOKS,
        UNIFORM,
        MEALS,
        EXCURSION,
        CLUBS
    }

    public enum PaymentFrequency {
        ANNUAL,
        TERMLY,
        MONTHLY,
        WEEKLY,
        ONE_TIME
    }
}

