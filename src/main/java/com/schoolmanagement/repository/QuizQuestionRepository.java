package com.schoolmanagement.repository;

import com.schoolmanagement.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    
    List<QuizQuestion> findByQuizIdAndIsActiveTrueOrderByQuestionOrder(Long quizId);
    
    @Query("SELECT q FROM QuizQuestion q WHERE q.quiz.id = :quizId AND q.isActive = true ORDER BY q.questionOrder")
    List<QuizQuestion> findActiveQuestionsByQuizOrderByOrder(@Param("quizId") Long quizId);
    
    @Query("SELECT COUNT(q) FROM QuizQuestion q WHERE q.quiz.id = :quizId AND q.isActive = true")
    Long countActiveQuestionsByQuiz(@Param("quizId") Long quizId);
}

