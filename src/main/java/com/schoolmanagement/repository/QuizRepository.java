package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findByClassEntityIdAndIsActiveTrue(Long classId);
    
    List<Quiz> findBySubjectIdAndIsActiveTrue(Long subjectId);
    
    List<Quiz> findByTeacherIdAndIsActiveTrue(Long teacherId);
    
    List<Quiz> findByAcademicYearIdAndIsActiveTrue(Long academicYearId);
    
    @Query("SELECT q FROM Quiz q WHERE q.classEntity.id = :classId AND q.status = 'PUBLISHED' AND q.isActive = true")
    List<Quiz> findActivePublishedQuizzesByClass(@Param("classId") Long classId);
    
    @Query("SELECT q FROM Quiz q WHERE q.classEntity.id = :classId AND q.startDate <= :now AND q.endDate >= :now AND q.isActive = true")
    List<Quiz> findOpenQuizzesByClass(@Param("classId") Long classId, @Param("now") LocalDateTime now);
    
    @Query("SELECT q FROM Quiz q WHERE q.teacher.id = :teacherId AND q.status = :status AND q.isActive = true")
    List<Quiz> findByTeacherIdAndStatusAndIsActiveTrue(@Param("teacherId") Long teacherId, @Param("status") Quiz.QuizStatus status);
    
    @Query("SELECT q FROM Quiz q WHERE q.classEntity.school.id = :schoolId AND q.isActive = true ORDER BY q.createdAt DESC")
    List<Quiz> findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(@Param("schoolId") Long schoolId);
}

