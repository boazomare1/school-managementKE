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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String employeeNumber;

    @Column(nullable = false, length = 50)
    private String tscNumber; // Teachers Service Commission number

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffType staffType; // TEACHER, ADMIN, SUPPORT, SECURITY, CLEANER, COOK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType; // PERMANENT, CONTRACT, PART_TIME, VOLUNTEER

    @Column(nullable = false)
    private LocalDate employmentDate;

    private LocalDate terminationDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basicSalary;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal houseAllowance;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal transportAllowance;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal medicalAllowance;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal otherAllowances = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nhifDeduction = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nssfDeduction = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal payeDeduction = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(length = 500)
    private String qualifications;

    @Column(length = 500)
    private String certifications;

    @Column(length = 1000)
    private String experience;

    @Column(length = 100)
    private String bankName;

    @Column(length = 50)
    private String bankAccount;

    @Column(length = 50)
    private String nhifNumber;

    @Column(length = 50)
    private String nssfNumber;

    @Column(length = 50)
    private String kraPin;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payroll> payrolls;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PerformanceReview> performanceReviews;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum StaffType {
        TEACHER,
        ADMIN,
        SUPPORT,
        SECURITY,
        CLEANER,
        COOK,
        LIBRARIAN,
        LAB_TECHNICIAN,
        COUNSELOR
    }

    public enum EmploymentType {
        PERMANENT,
        CONTRACT,
        PART_TIME,
        VOLUNTEER,
        INTERN
    }
}

