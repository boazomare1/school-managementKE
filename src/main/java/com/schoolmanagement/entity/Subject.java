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
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., "MATH", "ENG", "KIS", "SCI"

    @Column(nullable = false)
    private String name; // e.g., "Mathematics", "English", "Kiswahili", "Science"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurriculumType curriculumType; // EIGHT_FOUR_FOUR, CBC, CBE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectCategory category; // CORE, ELECTIVE, PRACTICAL, etc.

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer credits = 1; // Default credits for subjects

    // For CBC Learning Areas
    private String learningArea; // e.g., "Mathematical Activities", "Language Activities"
    private String competency; // e.g., "Number Work", "Reading"

    // For 8-4-4 System
    private String formLevel; // e.g., "Form 1-4", "Form 1-2"
    private String stream; // e.g., "Science", "Arts", "Technical"

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum CurriculumType {
        EIGHT_FOUR_FOUR, // Traditional 8-4-4 system
        CBC,            // Competency Based Curriculum
        CBE             // Competency Based Education
    }

    public enum SubjectCategory {
        CORE,           // Compulsory subjects
        ELECTIVE,       // Optional subjects
        PRACTICAL,      // Hands-on subjects
        LANGUAGE,       // Language subjects
        MATHEMATICAL,   // Math-related subjects
        SCIENTIFIC,     // Science subjects
        SOCIAL,         // Social studies
        CREATIVE,       // Arts and crafts
        PHYSICAL        // Physical education
    }
}