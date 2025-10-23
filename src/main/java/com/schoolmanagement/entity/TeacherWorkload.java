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
@Table(name = "teacher_workloads")
public class TeacherWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "academic_year_id", nullable = false)
    private Long academicYearId;

    @Column(name = "term_id", nullable = false)
    private Long termId;

    // Weekly workload tracking
    @Column(nullable = false)
    private Integer totalLessonsPerWeek = 0;

    @Column(nullable = false)
    private Integer totalMinutesPerWeek = 0;

    @Column(nullable = false)
    private Integer maxLessonsPerWeek = 40; // Ministry guideline: max 40 lessons per week

    @Column(nullable = false)
    private Integer maxMinutesPerWeek = 1600; // Ministry guideline: max 1600 minutes per week

    // Daily workload tracking
    @Column(nullable = false)
    private Integer maxLessonsPerDay = 8; // Ministry guideline: max 8 lessons per day

    @Column(nullable = false)
    private Integer maxMinutesPerDay = 320; // Ministry guideline: max 320 minutes per day

    // Subject distribution
    @Column(nullable = false)
    private Integer primarySubjectLessons = 0;

    @Column(nullable = false)
    private Integer secondarySubjectLessons = 0;

    @Column(nullable = false)
    private Integer optionalSubjectLessons = 0;

    // Special considerations
    @Column(nullable = false)
    private Boolean hasDoubleLessons = false;

    @Column(nullable = false)
    private Integer doubleLessonsCount = 0;

    @Column(nullable = false)
    private Boolean hasTripleLessons = false;

    @Column(nullable = false)
    private Integer tripleLessonsCount = 0;

    @Column(nullable = false)
    private Boolean isOverloaded = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Helper methods for workload validation
    public boolean isWithinWeeklyLimit() {
        return totalLessonsPerWeek <= maxLessonsPerWeek && totalMinutesPerWeek <= maxMinutesPerWeek;
    }

    public boolean isWithinDailyLimit(int lessonsToday, int minutesToday) {
        return lessonsToday <= maxLessonsPerDay && minutesToday <= maxMinutesPerDay;
    }

    public double getWeeklyUtilizationPercentage() {
        return (double) totalLessonsPerWeek / maxLessonsPerWeek * 100;
    }

    public double getDailyUtilizationPercentage(int lessonsToday) {
        return (double) lessonsToday / maxLessonsPerDay * 100;
    }

    public boolean hasReachedWeeklyLimit() {
        return totalLessonsPerWeek >= maxLessonsPerWeek || totalMinutesPerWeek >= maxMinutesPerWeek;
    }

    public boolean hasReachedDailyLimit(int lessonsToday, int minutesToday) {
        return lessonsToday >= maxLessonsPerDay || minutesToday >= maxMinutesPerDay;
    }

    public void updateWorkload(int lessonsToAdd, int minutesToAdd) {
        this.totalLessonsPerWeek += lessonsToAdd;
        this.totalMinutesPerWeek += minutesToAdd;
        this.isOverloaded = !isWithinWeeklyLimit();
    }

    public void resetWeeklyWorkload() {
        this.totalLessonsPerWeek = 0;
        this.totalMinutesPerWeek = 0;
        this.isOverloaded = false;
    }
}

