package com.schoolmanagement.repository;

import com.schoolmanagement.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    
    List<QuizAnswer> findBySubmissionIdAndIsActiveTrue(Long submissionId);
    
    List<QuizAnswer> findByQuestionIdAndIsActiveTrue(Long questionId);
    
    @Query("SELECT a FROM QuizAnswer a WHERE a.submission.id = :submissionId AND a.isActive = true ORDER BY a.question.questionOrder")
    List<QuizAnswer> findActiveAnswersBySubmissionOrderByQuestionOrder(@Param("submissionId") Long submissionId);
    
    @Query("SELECT a FROM QuizAnswer a WHERE a.submission.quiz.id = :quizId AND a.isActive = true")
    List<QuizAnswer> findActiveAnswersByQuiz(@Param("quizId") Long quizId);
    
    @Query("SELECT a FROM QuizAnswer a WHERE a.submission.student.id = :studentId AND a.isActive = true ORDER BY a.submission.submittedAt DESC")
    List<QuizAnswer> findActiveAnswersByStudentOrderBySubmissionDateDesc(@Param("studentId") Long studentId);
    
    @Query("SELECT a FROM QuizAnswer a WHERE a.question.id = :questionId AND a.isCorrect = true AND a.isActive = true")
    List<QuizAnswer> findCorrectAnswersByQuestion(@Param("questionId") Long questionId);
    
    @Query("SELECT COUNT(a) FROM QuizAnswer a WHERE a.submission.quiz.id = :quizId AND a.isCorrect = true AND a.isActive = true")
    Long countCorrectAnswersByQuiz(@Param("quizId") Long quizId);
}

