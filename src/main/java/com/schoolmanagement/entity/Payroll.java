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
import java.util.List;

@Entity
@Table(name = "payrolls")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payroll {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_staff_id", nullable = false)
    private SupportStaff supportStaff;
    
    @Column(nullable = false)
    private LocalDate payPeriodStart;
    
    @Column(nullable = false)
    private LocalDate payPeriodEnd;
    
    @Column(nullable = false)
    private LocalDate payDate;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basicSalary;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal overtimePay;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAllowances;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal bonuses;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal grossPay;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal taxDeduction;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal socialSecurityDeduction;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal otherDeductions;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal totalDeductions;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netPay;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status;
    
    @Column(length = 100)
    private String paymentMethod; // e.g., "Bank Transfer", "Cash", "Check"
    
    @Column(length = 50)
    private String transactionReference;
    
    @Column(length = 1000)
    private String notes;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PayrollDeduction> deductions;
    
    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PayrollAllowance> allowances;
    
    public enum PayrollStatus {
        DRAFT,
        PENDING_APPROVAL,
        APPROVED,
        PROCESSED,
        PAID,
        CANCELLED
    }
}