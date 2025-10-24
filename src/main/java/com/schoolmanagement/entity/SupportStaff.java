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
@Table(name = "support_staff")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportStaff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The User entity representing the support staff
    
    @Column(nullable = false, length = 50)
    private String employeeId; // Unique employee ID
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportStaffType staffType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus employmentStatus;
    
    @Column(nullable = false)
    private LocalDate hireDate;
    
    private LocalDate terminationDate;
    
    @Column(length = 500)
    private String terminationReason;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basicSalary;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal allowances;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal deductions;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netSalary;
    
    @Column(length = 100)
    private String department;
    
    @Column(length = 100)
    private String position;
    
    @Column(length = 100)
    private String supervisor;
    
    @Column(length = 50)
    private String workSchedule; // e.g., "Full-time", "Part-time", "Shift"
    
    @Column(length = 20)
    private String employmentType; // e.g., "Permanent", "Contract", "Temporary"
    
    @Column(length = 50)
    private String bankName;
    
    @Column(length = 50)
    private String bankAccount;
    
    @Column(length = 20)
    private String nationalId;
    
    @Column(length = 20)
    private String socialSecurityNumber;
    
    @Column(length = 20)
    private String taxPin;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(length = 1000)
    private String notes;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "supportStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payroll> payrolls;
    
    @OneToMany(mappedBy = "supportStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SalaryAdjustment> salaryAdjustments;
    
    @OneToMany(mappedBy = "supportStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeaveRequest> leaveRequests;
    
    public enum SupportStaffType {
        SECURITY_GUARD,
        CLEANER,
        MAINTENANCE,
        COOK,
        DRIVER,
        GARDENER,
        RECEPTIONIST,
        CLERK,
        LIBRARIAN,
        NURSE,
        COUNSELOR,
        IT_SUPPORT,
        ACCOUNTANT,
        ADMINISTRATOR,
        OTHER
    }
    
    public enum EmploymentStatus {
        ACTIVE,
        ON_LEAVE,
        SUSPENDED,
        TERMINATED,
        RETIRED,
        RESIGNED
    }
}
