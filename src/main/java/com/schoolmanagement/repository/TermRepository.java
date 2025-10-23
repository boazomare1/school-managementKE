package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    
    List<Term> findByAcademicYearIdAndIsActiveTrueOrderBySequenceAsc(Long academicYearId);
    
    List<Term> findByIsActiveTrueOrderByStartDateAsc();
    
    Optional<Term> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT t FROM Term t WHERE t.academicYear.id = :academicYearId AND t.isActive = true AND t.startDate <= :date AND t.endDate >= :date")
    Optional<Term> findCurrentTermByAcademicYearAndDate(@Param("academicYearId") Long academicYearId, @Param("date") LocalDate date);
    
    @Query("SELECT t FROM Term t WHERE t.academicYear.id = :academicYearId AND t.isActive = true ORDER BY t.sequence ASC")
    List<Term> findActiveTermsByAcademicYearOrderBySequence(@Param("academicYearId") Long academicYearId);
    
    @Query("SELECT COUNT(t) FROM Term t WHERE t.academicYear.id = :academicYearId AND t.isActive = true")
    Long countByAcademicYearIdAndIsActiveTrue(@Param("academicYearId") Long academicYearId);
}