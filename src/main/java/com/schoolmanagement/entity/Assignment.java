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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @Column(nullable = false)
    private LocalDateTime assignedDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal totalMarks;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal passingMarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentType assignmentType; // HOMEWORK, PROJECT, ESSAY, QUIZ, PRESENTATION

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status; // DRAFT, PUBLISHED, CLOSED, GRADED

    private String instructions;

    private String submissionFormat; // PDF, DOC, IMAGE, etc.

    private Integer maxAttempts = 1;

    private Boolean allowLateSubmission = false;

    private Integer latePenaltyPercentage = 0;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum AssignmentType {
        HOMEWORK,
        PROJECT,
        ESSAY,
        QUIZ,
        PRESENTATION,
        LAB_REPORT,
        RESEARCH_PAPER
    }

    public enum AssignmentStatus {
        DRAFT,
        PUBLISHED,
        CLOSED,
        GRADED
    }
}

