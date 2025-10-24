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

@Entity
@Table(name = "salary_adjustments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryAdjustment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_staff_id", nullable = false)
    private SupportStaff supportStaff;
    
    @Column(nullable = false)
    private LocalDate effectiveDate;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal previousSalary;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal newSalary;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal adjustmentAmount;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal adjustmentPercentage;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentType adjustmentType;
    
    @Column(length = 500)
    private String reason;
    
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
    
    public enum AdjustmentType {
        INCREMENT,
        DECREMENT,
        PROMOTION,
        DEMOTION,
        PERFORMANCE_BONUS,
        COST_OF_LIVING,
        MARKET_ADJUSTMENT,
        OTHER
    }
}
