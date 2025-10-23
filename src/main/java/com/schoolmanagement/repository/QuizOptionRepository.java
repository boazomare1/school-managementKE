package com.schoolmanagement.repository;

import com.schoolmanagement.entity.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Long> {
    
    List<QuizOption> findByQuestionIdAndIsActiveTrueOrderByOptionOrder(Long questionId);
    
    @Query("SELECT o FROM QuizOption o WHERE o.question.id = :questionId AND o.isActive = true ORDER BY o.optionOrder")
    List<QuizOption> findActiveOptionsByQuestionOrderByOrder(@Param("questionId") Long questionId);
    
    @Query("SELECT o FROM QuizOption o WHERE o.question.id = :questionId AND o.isCorrect = true AND o.isActive = true")
    List<QuizOption> findCorrectOptionsByQuestion(@Param("questionId") Long questionId);
}

