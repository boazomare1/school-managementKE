package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parent_guardians")
public class ParentGuardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationshipType relationshipType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityType priorityType;

    @Column(nullable = false)
    private Boolean isPrimaryContact = false;

    @Column(nullable = false)
    private Boolean isEmergencyContact = false;

    @Column(nullable = false)
    private Boolean canPickupStudent = false;

    @Column(nullable = false)
    private Boolean canReceiveNotifications = true;

    @Column(nullable = false)
    private Boolean isActive = true;

    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum RelationshipType {
        FATHER,
        MOTHER,
        STEP_FATHER,
        STEP_MOTHER,
        GRANDFATHER,
        GRANDMOTHER,
        UNCLE,
        AUNT,
        SIBLING,
        GUARDIAN,
        OTHER
    }

    public enum PriorityType {
        PRIMARY,    // First priority parent/guardian
        SECONDARY,  // Second priority parent/guardian  
        TERTIARY    // Third priority (guardian only)
    }
}

