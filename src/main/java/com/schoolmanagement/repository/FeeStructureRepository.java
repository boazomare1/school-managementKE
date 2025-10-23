package com.schoolmanagement.repository;

import com.schoolmanagement.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    
    List<FeeStructure> findBySchoolIdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long academicYearId);
    
    List<FeeStructure> findBySchoolIdAndClassEntityIdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long classId, Long academicYearId);
    
    Optional<FeeStructure> findBySchoolIdAndNameAndIsActiveTrue(Long schoolId, String name);
    
    @Query("SELECT fs FROM FeeStructure fs WHERE fs.school.id = :schoolId AND fs.academicYear.id = :academicYearId AND fs.isActive = true AND fs.effectiveFrom <= :currentDate AND fs.effectiveTo >= :currentDate ORDER BY fs.name")
    List<FeeStructure> findActiveFeeStructuresBySchoolAndAcademicYear(Long schoolId, Long academicYearId, LocalDateTime currentDate);
}
