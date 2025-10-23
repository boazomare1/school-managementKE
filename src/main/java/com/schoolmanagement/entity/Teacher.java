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
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The User entity representing the teacher

    @Column(nullable = false, unique = true)
    private String tscNumber; // Teachers Service Commission number

    @Column(nullable = false)
    private String department; // Math, Science, Languages, etc.

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeacherSpecialization> specializations; // Teacher's subject specializations

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeacherRole role; // CLASS_TEACHER, SUBJECT_TEACHER, HOD, DEPUTY_PRINCIPAL, PRINCIPAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity assignedClass; // If they are a class teacher

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dormitory_id")
    private Dormitory assignedDormitory; // If they are a dorm master

    @Column(nullable = false)
    private Boolean isActive = true;

    private String qualifications;
    private String experience;
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum TeacherRole {
        CLASS_TEACHER,
        SUBJECT_TEACHER,
        HOD, // Head of Department
        DEPUTY_PRINCIPAL,
        PRINCIPAL
    }
}
