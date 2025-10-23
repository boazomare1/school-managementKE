package com.schoolmanagement.repository;

import com.schoolmanagement.entity.LessonPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LessonPlanRepository extends JpaRepository<LessonPlan, Long> {

    List<LessonPlan> findByTeacherIdAndIsActiveTrueOrderByLessonDateDesc(Long teacherId);

    List<LessonPlan> findByClassEntityIdAndIsActiveTrueOrderByLessonDateDesc(Long classId);

    List<LessonPlan> findBySubjectIdAndIsActiveTrueOrderByLessonDateDesc(Long subjectId);

    List<LessonPlan> findByStatusAndIsActiveTrue(LessonPlan.LessonStatus status);

    @Query("SELECT lp FROM LessonPlan lp WHERE lp.classEntity.id = :classId AND lp.subject.id = :subjectId AND lp.isActive = true ORDER BY lp.lessonDate DESC")
    List<LessonPlan> findByClassAndSubject(Long classId, Long subjectId);

    @Query("SELECT lp FROM LessonPlan lp WHERE lp.teacher.id = :teacherId AND lp.status = :status AND lp.isActive = true ORDER BY lp.lessonDate DESC")
    List<LessonPlan> findByTeacherAndStatus(Long teacherId, LessonPlan.LessonStatus status);

    @Query("SELECT lp FROM LessonPlan lp WHERE lp.lessonDate = :date AND lp.isActive = true ORDER BY lp.lessonDate")
    List<LessonPlan> findByLessonDate(LocalDate date);

    @Query("SELECT lp FROM LessonPlan lp WHERE lp.lessonDate BETWEEN :startDate AND :endDate AND lp.isActive = true ORDER BY lp.lessonDate")
    List<LessonPlan> findByLessonDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT lp FROM LessonPlan lp WHERE lp.teacher.id = :teacherId AND lp.lessonDate = :date AND lp.isActive = true ORDER BY lp.lessonDate")
    List<LessonPlan> findByTeacherAndDate(Long teacherId, LocalDate date);

    @Query("SELECT COUNT(lp) FROM LessonPlan lp WHERE lp.teacher.id = :teacherId AND lp.isActive = true")
    Long countByTeacher(Long teacherId);
}

