package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    List<Exam> findBySubjectIdAndIsActiveTrue(Long subjectId);
    
    List<Exam> findByTermIdAndIsActiveTrue(Long termId);
    
    @Query("SELECT COUNT(e) FROM Exam e WHERE e.isActive = true")
    Long countBySchoolId(Long schoolId);
    
    @Query("SELECT COUNT(e) FROM Exam e WHERE e.isActive = true AND e.examDate < CURRENT_DATE")
    Long countCompletedBySchoolId(Long schoolId);
}
