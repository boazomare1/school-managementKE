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
@Table(name = "teacher_specializations")
public class TeacherSpecialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecializationLevel level; // PRIMARY, SECONDARY, OPTIONAL, INTEREST

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecializationType type; // ACADEMIC, DRAMA, MUSIC, ATHLETICS, etc.

    @Column(nullable = false)
    private Boolean isActive = true;

    private String qualifications; // e.g., "B.Ed Mathematics", "M.Ed Science"
    private String experience; // e.g., "5 years teaching Mathematics"
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum SpecializationLevel {
        PRIMARY,    // Main specialization (required)
        SECONDARY,  // Secondary specialization (required)
        OPTIONAL,   // Additional subjects they can teach
        INTEREST    // Personal interests and special roles (unlimited)
    }

    public enum SpecializationType {
        // Academic Subjects
        ACADEMIC,           // Regular academic subjects
        
        // Creative Arts
        DRAMA,              // Drama and Theatre
        MUSIC,              // Music and Performing Arts
        ART,                // Visual Arts and Design
        DANCE,              // Dance and Movement
        
        // Sports and Athletics
        ATHLETICS,          // Track and Field
        FOOTBALL,           // Soccer
        BASKETBALL,         // Basketball
        VOLLEYBALL,         // Volleyball
        TENNIS,             // Tennis
        SWIMMING,           // Swimming
        RUGBY,              // Rugby
        CRICKET,            // Cricket
        
        // Extracurricular Activities
        DEBATE,             // Debate and Public Speaking
        JOURNALISM,         // School Newspaper/Media
        PHOTOGRAPHY,        // Photography Club
        ROBOTICS,           // Robotics and Technology
        ENVIRONMENTAL,      // Environmental Club
        COMMUNITY_SERVICE,  // Community Service
        
        // Special Programs
        GIFTED_EDUCATION,   // Gifted and Talented Programs
        SPECIAL_NEEDS,      // Special Education
        CAREER_GUIDANCE,    // Career Counseling
        PSYCHOLOGY,         // School Psychology
        COUNSELING,         // Student Counseling
        
        // Administrative Roles
        HEAD_OF_DEPARTMENT, // Department Head
        COORDINATOR,        // Program Coordinator
        SUPERVISOR,         // Academic Supervisor
        MENTOR              // Teacher Mentor
    }
}
