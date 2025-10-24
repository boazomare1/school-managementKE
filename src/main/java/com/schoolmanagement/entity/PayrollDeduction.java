package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_deductions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDeduction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;
    
    @Column(nullable = false, length = 100)
    private String deductionType;
    
    @Column(length = 200)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private Boolean isMandatory = false;
    
    @Column(length = 100)
    private String reference;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum DeductionType {
        TAX,
        SOCIAL_SECURITY,
        PENSION,
        HEALTH_INSURANCE,
        LOAN_DEDUCTION,
        ADVANCE_DEDUCTION,
        UNIFORM_DEDUCTION,
        OTHER
    }
}
