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
@Table(name = "cbc_competencies")
public class CbcCompetency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String competencyCode; // e.g., "MA1.1.1"

    @Column(nullable = false)
    private String competencyName;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeLevel gradeLevel; // Grade 1-6 for primary, Form 1-4 for secondary

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LearningArea learningArea; // Mathematics, English, Kiswahili, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Strand strand; // Number, Measurement, Geometry, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubStrand subStrand; // Number concept, Addition, etc.

    @Column(nullable = false)
    private Integer sequence; // Order within the strand

    @Column(nullable = false)
    private Boolean isCoreCompetency = true;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum GradeLevel {
        GRADE_1, GRADE_2, GRADE_3, GRADE_4, GRADE_5, GRADE_6,
        FORM_1, FORM_2, FORM_3, FORM_4
    }

    public enum LearningArea {
        MATHEMATICS,
        ENGLISH,
        KISWAHILI,
        SCIENCE,
        SOCIAL_STUDIES,
        RELIGIOUS_EDUCATION,
        CREATIVE_ARTS,
        PHYSICAL_EDUCATION,
        AGRICULTURE,
        HOME_SCIENCE,
        BUSINESS_STUDIES,
        COMPUTER_STUDIES
    }

    public enum Strand {
        NUMBER,
        MEASUREMENT,
        GEOMETRY,
        DATA_HANDLING,
        ALGEBRA,
        READING,
        WRITING,
        LISTENING,
        SPEAKING,
        GRAMMAR,
        LITERATURE
    }

    public enum SubStrand {
        NUMBER_CONCEPT,
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        FRACTIONS,
        DECIMALS,
        PERCENTAGES,
        LENGTH,
        MASS,
        CAPACITY,
        TIME,
        MONEY,
        SHAPES,
        SPACE,
        DATA_COLLECTION,
        DATA_REPRESENTATION,
        DATA_ANALYSIS,
        ALGEBRAIC_EXPRESSIONS,
        EQUATIONS,
        INEQUALITIES,
        PHONICS,
        COMPREHENSION,
        CREATIVE_WRITING,
        GRAMMAR_RULES,
        POETRY,
        PROSE,
        DRAMA
    }
}

