package com.schoolmanagement.repository;

import com.schoolmanagement.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    
    List<AcademicYear> findBySchoolIdAndIsActiveTrue(Long schoolId);
    
    Optional<AcademicYear> findBySchoolIdAndIsActiveTrueAndId(Long schoolId, Long id);
    
    @Query("SELECT ay FROM AcademicYear ay WHERE ay.school.id = :schoolId ORDER BY ay.startDate DESC")
    List<AcademicYear> findBySchoolIdOrderByStartDateDesc(Long schoolId);
}


