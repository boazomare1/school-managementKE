package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timetables")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_id")
    private Stream stream; // Optional - for schools with streams

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer duration; // Duration in minutes (typically 40 minutes)

    @Column(nullable = false)
    private String room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType lessonType;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "academic_year_id")
    private Long academicYearId;

    @Column(name = "term_id")
    private Long termId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum LessonType {
        SINGLE,     // Single lesson (40 minutes)
        DOUBLE,     // Double lesson (80 minutes) - allowed by ministry guidelines
        TRIPLE,     // Triple lesson (120 minutes) - for practical subjects
        CONTINUOUS  // Continuous lesson (e.g., field trips, practicals)
    }

    // Helper methods for timetable validation
    public boolean isDoubleLesson() {
        return lessonType == LessonType.DOUBLE;
    }

    public boolean isTripleLesson() {
        return lessonType == LessonType.TRIPLE;
    }

    public int getActualDuration() {
        return switch (lessonType) {
            case SINGLE -> 40;
            case DOUBLE -> 80;
            case TRIPLE -> 120;
            case CONTINUOUS -> duration;
        };
    }

    public boolean isMorningSession() {
        return startTime.isBefore(LocalTime.of(12, 0));
    }

    public boolean isAfternoonSession() {
        return startTime.isAfter(LocalTime.of(12, 0));
    }

    public boolean isWithinSchoolHours() {
        LocalTime schoolStart = LocalTime.of(8, 10);
        LocalTime schoolEnd = LocalTime.of(16, 0);
        return !startTime.isBefore(schoolStart) && !endTime.isAfter(schoolEnd);
    }
}