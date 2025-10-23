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
@Table(name = "capitation_grants")
public class CapitationGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(nullable = false)
    private String grantNumber;

    @Column(nullable = false)
    private LocalDate applicationDate;

    @Column(nullable = false)
    private LocalDate approvalDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal receivedAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pendingAmount;

    @Column(nullable = false)
    private LocalDate expectedDate;

    private LocalDate receivedDate;

    @Column(nullable = false)
    private Integer studentCount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal perStudentAmount;

    @Column(length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GrantStatus status; // PENDING, APPROVED, DISBURSED, UTILIZED, COMPLETED

    @Column(length = 1000)
    private String utilizationReport;

    @Column(length = 1000)
    private String auditNotes;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum GrantStatus {
        PENDING,
        APPROVED,
        DISBURSED,
        UTILIZED,
        COMPLETED,
        AUDITED
    }
}