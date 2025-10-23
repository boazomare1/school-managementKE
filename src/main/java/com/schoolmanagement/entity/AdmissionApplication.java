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
@Table(name = "admission_applications")
public class AdmissionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String applicationNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String middleName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false)
    private String idNumber;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String previousSchool;

    @Column(nullable = false)
    private String previousSchoolLocation;

    @Column(nullable = false)
    private String previousClass;

    @Column(nullable = false)
    private String kcpeIndex; // For secondary school applications

    @Column(nullable = false)
    private BigDecimal kcpeMarks;

    @Column(nullable = false)
    private String kcpeYear;

    @Column(nullable = false)
    private String parentName;

    @Column(nullable = false)
    private String parentPhone;

    @Column(nullable = false)
    private String parentEmail;

    @Column(nullable = false)
    private String parentOccupation;

    @Column(nullable = false)
    private String parentAddress;

    @Column(nullable = false)
    private String guardianName;

    @Column(nullable = false)
    private String guardianPhone;

    @Column(nullable = false)
    private String guardianEmail;

    @Column(nullable = false)
    private String guardianRelationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applying_class_id", nullable = false)
    private ClassEntity applyingClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status; // PENDING, UNDER_REVIEW, ACCEPTED, REJECTED, WITHDRAWN

    @Column(nullable = false)
    private LocalDate applicationDate;

    private LocalDate reviewDate;

    private LocalDate decisionDate;

    @Column(length = 1000)
    private String reviewNotes;

    @Column(length = 1000)
    private String decisionReason;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal applicationFee;

    @Column(nullable = false)
    private Boolean applicationFeePaid = false;

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ApplicationStatus {
        PENDING,
        UNDER_REVIEW,
        ACCEPTED,
        REJECTED,
        WITHDRAWN
    }
}

