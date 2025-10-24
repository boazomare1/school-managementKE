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

@Entity
@Table(name = "payroll_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // e.g., "House Allowance", "PAYE", "NSSF"
    
    @Column(nullable = false, length = 50)
    private String code; // e.g., "HOUSE_ALLOW", "PAYE", "NSSF"
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollItemType type; // ALLOWANCE or DEDUCTION
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isMandatory = false; // For statutory deductions
    
    @Column(nullable = false)
    private Boolean isPercentage = false; // If true, amount is percentage
    
    @Column(precision = 10, scale = 2)
    private BigDecimal fixedAmount; // Fixed amount if not percentage
    
    @Column(precision = 5, scale = 2)
    private BigDecimal percentageRate; // Percentage rate (e.g., 15.5 for 15.5%)
    
    @Column(precision = 10, scale = 2)
    private BigDecimal minimumAmount; // Minimum amount for percentage calculations
    
    @Column(precision = 10, scale = 2)
    private BigDecimal maximumAmount; // Maximum amount for percentage calculations
    
    @Column(nullable = false)
    private Boolean isTaxable = true; // Whether this item affects tax calculation
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 100)
    private String category; // e.g., "STATUTORY", "BENEFITS", "PENALTIES"
    
    @Column(length = 50)
    private String governmentCode; // Government code for statutory items
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum PayrollItemType {
        ALLOWANCE,
        DEDUCTION
    }
}
