package com.schoolmanagement.repository;

import com.schoolmanagement.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    
    List<ClassEntity> findBySchoolIdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long academicYearId);
    
    Optional<ClassEntity> findBySchoolIdAndAcademicYearIdAndNameAndIsActiveTrue(Long schoolId, Long academicYearId, String name);
    
    @Query("SELECT c FROM ClassEntity c WHERE c.school.id = :schoolId AND c.academicYear.id = :academicYearId AND c.isActive = true ORDER BY c.name")
    List<ClassEntity> findActiveClassesBySchoolAndAcademicYear(Long schoolId, Long academicYearId);
}


