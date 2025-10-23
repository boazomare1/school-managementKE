package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Bursary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BursaryRepository extends JpaRepository<Bursary, Long> {
    
    List<Bursary> findByStudentIdAndIsActiveTrueOrderByApplicationDateDesc(Long studentId);
    
    List<Bursary> findBySchoolIdAndIsActiveTrueOrderByApplicationDateDesc(Long schoolId);
    
    List<Bursary> findByAcademicYearIdAndIsActiveTrueOrderByApplicationDateDesc(Long academicYearId);
    
    List<Bursary> findByStatusAndIsActiveTrueOrderByApplicationDateDesc(Bursary.BursaryStatus status);
    
    Optional<Bursary> findByBursaryNumberAndIsActiveTrue(String bursaryNumber);
    
    @Query("SELECT b FROM Bursary b WHERE b.student.id = :studentId AND b.academicYear.id = :academicYearId AND b.isActive = true ORDER BY b.applicationDate DESC")
    List<Bursary> findByStudentIdAndAcademicYearIdAndIsActiveTrueOrderByApplicationDateDesc(
        @Param("studentId") Long studentId,
        @Param("academicYearId") Long academicYearId
    );
    
    @Query("SELECT SUM(b.approvedAmount) FROM Bursary b WHERE b.school.id = :schoolId AND b.academicYear.id = :academicYearId AND b.isActive = true")
    Double getTotalApprovedAmountBySchoolAndAcademicYear(@Param("schoolId") Long schoolId, @Param("academicYearId") Long academicYearId);
    
    @Query("SELECT COUNT(b) FROM Bursary b WHERE b.student.id = :studentId AND b.isActive = true")
    Long countByStudentIdAndIsActiveTrue(@Param("studentId") Long studentId);
}

