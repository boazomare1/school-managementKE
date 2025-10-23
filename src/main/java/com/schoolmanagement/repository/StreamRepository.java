package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Stream;
import com.schoolmanagement.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {

    List<Stream> findByClassEntityAndIsActiveTrue(ClassEntity classEntity);
    
    List<Stream> findByClassEntityAndAcademicYearIdAndIsActiveTrue(ClassEntity classEntity, Long academicYearId);
    
    List<Stream> findByAcademicYearIdAndIsActiveTrue(Long academicYearId);
    
    Optional<Stream> findByNameAndClassEntityAndIsActiveTrue(String name, ClassEntity classEntity);
    
    Optional<Stream> findByCodeAndClassEntityAndIsActiveTrue(String code, ClassEntity classEntity);
    
    List<Stream> findByIsActiveTrue();
}

