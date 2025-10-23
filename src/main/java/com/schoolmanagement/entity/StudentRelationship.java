package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_relationships")
public class StudentRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Academic Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_class_id")
    private ClassEntity currentClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    private User classTeacher;

    // Enrollment Information
    @Column(nullable = false)
    private String enrollmentNumber;

    @Column(nullable = false)
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus enrollmentStatus;

    // Academic Progress
    @Column(nullable = false)
    private String currentGrade;

    @Column(nullable = false)
    private Integer academicYearProgress; // 1, 2, 3, etc.

    @Column(nullable = false)
    private Boolean isPromoted = false;

    @Column(nullable = false)
    private Boolean isGraduated = false;

    // Parent/Guardian Relationships (Maximum 3: 2 parents + 1 guardian)
    // Note: ParentGuardian has direct relationship to User (student)
    // No need for StudentRelationship -> ParentGuardian mapping

    // Academic Records
    // Note: StudentSubject has direct relationship to User (student)
    // No need for StudentRelationship -> StudentSubject mapping

    // Note: StudentGrade has direct relationship to User (student)
    // No need for StudentRelationship -> StudentGrade mapping

    // Note: StudentAttendance has direct relationship to User (student)
    // No need for StudentRelationship -> StudentAttendance mapping

    // Note: StudentExam has direct relationship to User (student)
    // No need for StudentRelationship -> StudentExam mapping

    // Note: StudentFee has direct relationship to User (student)
    // No need for StudentRelationship -> StudentFee mapping

    // System Fields
    @Column(nullable = false)
    private Boolean isActive = true;

    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum EnrollmentStatus {
        ENROLLED,
        TRANSFERRED,
        GRADUATED,
        DROPPED,
        SUSPENDED,
        EXPELLED
    }

    // Helper methods for relationship validation
    // Note: These methods are now simplified since parentGuardians relationship was removed
    // to avoid circular JPA mapping issues. Parent/Guardian relationships are managed
    // directly through the ParentGuardian entity.
    
    public boolean canAddParentGuardian() {
        // This would need to be implemented through a service that queries the database
        // to check the actual count of parent guardians for this student
        return true; // Default implementation
    }

    public boolean hasPrimaryContact() {
        // This would need to be implemented through a service that queries the database
        return false; // Default implementation
    }

    public boolean hasEmergencyContact() {
        // This would need to be implemented through a service that queries the database
        return false; // Default implementation
    }

    public List<ParentGuardian> getActiveParentGuardians() {
        // This would need to be implemented through a service that queries the database
        return List.of(); // Default implementation
    }

    public List<ParentGuardian> getNotificationRecipients() {
        // This would need to be implemented through a service that queries the database
        return List.of(); // Default implementation
    }
}
