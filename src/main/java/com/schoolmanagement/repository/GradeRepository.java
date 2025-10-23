package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByEnrollmentId(Long enrollmentId);
    
    List<Grade> findByExamId(Long examId);
    
    @Query("SELECT g.grade, COUNT(g) FROM Grade g WHERE g.enrollment.classEntity.school.id = :schoolId GROUP BY g.grade")
    List<Object[]> getPerformanceDistributionBySchool(Long schoolId);
}
