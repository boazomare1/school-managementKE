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
@Table(name = "student_competencies")
public class StudentCompetency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competency_id", nullable = false)
    private CbcCompetency competency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompetencyLevel level; // EXCEEDS, MEETS, APPROACHING, BELOW

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentType assessmentType; // FORMATIVE, SUMMATIVE, CONTINUOUS

    private String evidence; // Description of evidence

    private String teacherComments;

    private String nextSteps; // Recommendations for improvement

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum CompetencyLevel {
        EXCEEDS,    // 4 - Exceeds expectations
        MEETS,      // 3 - Meets expectations
        APPROACHING, // 2 - Approaching expectations
        BELOW       // 1 - Below expectations
    }

    public enum AssessmentType {
        FORMATIVE,
        SUMMATIVE,
        CONTINUOUS,
        OBSERVATION,
        PORTFOLIO
    }
}

