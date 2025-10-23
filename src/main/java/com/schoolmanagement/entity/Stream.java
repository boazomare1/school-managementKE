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
@Table(name = "streams")
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "A", "B", "C", "North", "South", "East", "West"

    @Column(nullable = false)
    private String code; // e.g., "A", "B", "C", "N", "S", "E", "W"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @Column(nullable = false)
    private Integer capacity; // Maximum number of students in this stream

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "academic_year_id")
    private Long academicYearId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Helper methods
    public String getFullName() {
        return classEntity.getName() + " " + name;
    }

    public String getFullCode() {
        return classEntity.getCode() + name;
    }
}

