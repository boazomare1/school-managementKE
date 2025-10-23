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
@Table(name = "payroll")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false)
    private LocalDate payPeriodStart;

    @Column(nullable = false)
    private LocalDate payPeriodEnd;

    @Column(nullable = false)
    private LocalDate payDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basicSalary;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal houseAllowance;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal transportAllowance;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal medicalAllowance;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal otherAllowances;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal grossSalary;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nhifDeduction;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nssfDeduction;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal payeDeduction;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal otherDeductions;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDeductions;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status; // PENDING, PROCESSED, PAID, CANCELLED

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum PayrollStatus {
        PENDING,
        PROCESSED,
        PAID,
        CANCELLED
    }
}

