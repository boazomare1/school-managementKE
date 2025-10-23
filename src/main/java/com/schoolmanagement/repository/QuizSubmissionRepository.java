package com.schoolmanagement.repository;

import com.schoolmanagement.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    
    List<QuizSubmission> findByStudentIdAndIsActiveTrueOrderBySubmittedAtDesc(Long studentId);
    
    List<QuizSubmission> findByQuizIdAndIsActiveTrueOrderBySubmittedAtDesc(Long quizId);
    
    Optional<QuizSubmission> findByQuizIdAndStudentIdAndIsActiveTrue(Long quizId, Long studentId);
    
    @Query("SELECT s FROM QuizSubmission s WHERE s.quiz.id = :quizId AND s.student.id = :studentId AND s.isActive = true")
    Optional<QuizSubmission> findActiveSubmissionByQuizAndStudent(@Param("quizId") Long quizId, @Param("studentId") Long studentId);
    
    @Query("SELECT s FROM QuizSubmission s WHERE s.quiz.classEntity.id = :classId AND s.isActive = true ORDER BY s.submittedAt DESC")
    List<QuizSubmission> findActiveSubmissionsByClassOrderBySubmittedAtDesc(@Param("classId") Long classId);
    
    @Query("SELECT s FROM QuizSubmission s WHERE s.quiz.teacher.id = :teacherId AND s.isActive = true ORDER BY s.submittedAt DESC")
    List<QuizSubmission> findActiveSubmissionsByTeacherOrderBySubmittedAtDesc(@Param("teacherId") Long teacherId);
    
    @Query("SELECT COUNT(s) FROM QuizSubmission s WHERE s.quiz.id = :quizId AND s.isActive = true")
    Long countActiveSubmissionsByQuiz(@Param("quizId") Long quizId);
    
    @Query("SELECT s FROM QuizSubmission s WHERE s.quiz.id = :quizId AND s.status = :status AND s.isActive = true")
    List<QuizSubmission> findByQuizIdAndStatusAndIsActiveTrue(@Param("quizId") Long quizId, @Param("status") QuizSubmission.SubmissionStatus status);
}

