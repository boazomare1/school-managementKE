package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nemis_students")
public class NemisStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, unique = true)
    private String nemisNumber; // NEMIS unique identifier

    @Column(nullable = false)
    private String upiNumber; // Unique Personal Identifier

    @Column(nullable = false)
    private String birthCertificateNumber;

    private String nationalIdNumber; // For parents/guardians

    @Column(nullable = false)
    private String county;

    @Column(nullable = false)
    private String subCounty;

    @Column(nullable = false)
    private String ward;

    private String constituency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisabilityStatus disabilityStatus;

    private String disabilityDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrphanStatus orphanStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VulnerableStatus vulnerableStatus;

    @Column(nullable = false)
    private Boolean isSpecialNeeds = false;

    private String specialNeedsDescription;

    @Column(nullable = false)
    private Boolean isOrphan = false;

    @Column(nullable = false)
    private Boolean isVulnerable = false;

    @Column(nullable = false)
    private Boolean isBursaryRecipient = false;

    private String bursarySource;

    @Column(nullable = false)
    private Boolean isCapitationRecipient = true; // Most students receive capitation

    @Column(nullable = false)
    private Boolean isNemisSynced = false;

    private LocalDateTime lastNemisSync;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum DisabilityStatus {
        NONE,
        PHYSICAL,
        VISUAL,
        HEARING,
        INTELLECTUAL,
        MULTIPLE
    }

    public enum OrphanStatus {
        NONE,
        SINGLE_ORPHAN,
        DOUBLE_ORPHAN
    }

    public enum VulnerableStatus {
        NONE,
        VULNERABLE,
        HIGHLY_VULNERABLE
    }
}

