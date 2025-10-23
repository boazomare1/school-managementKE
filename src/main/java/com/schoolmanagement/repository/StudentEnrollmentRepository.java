package com.schoolmanagement.repository;

import com.schoolmanagement.entity.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {
    
    List<StudentEnrollment> findByClassEntityIdAndIsActiveTrue(Long classId);
    
    List<StudentEnrollment> findByStudentIdAndIsActiveTrue(Long studentId);
    
    Optional<StudentEnrollment> findByEnrollmentNumber(String enrollmentNumber);
    
    @Query("SELECT se FROM StudentEnrollment se WHERE se.classEntity.id = :classId AND se.isActive = true ORDER BY se.student.firstName, se.student.lastName")
    List<StudentEnrollment> findActiveEnrollmentsByClass(Long classId);
    
    @Query("SELECT se FROM StudentEnrollment se WHERE se.student.id = :studentId AND se.isActive = true ORDER BY se.enrollmentDate DESC")
    List<StudentEnrollment> findActiveEnrollmentsByStudent(Long studentId);
}


