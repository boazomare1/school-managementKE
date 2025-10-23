package com.schoolmanagement.repository;

import com.schoolmanagement.entity.CapitationGrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CapitationGrantRepository extends JpaRepository<CapitationGrant, Long> {
    
    List<CapitationGrant> findBySchoolIdAndIsActiveTrueOrderByApplicationDateDesc(Long schoolId);
    
    List<CapitationGrant> findByAcademicYearIdAndIsActiveTrueOrderByApplicationDateDesc(Long academicYearId);
    
    List<CapitationGrant> findByStatusAndIsActiveTrueOrderByApplicationDateDesc(CapitationGrant.GrantStatus status);
    
    Optional<CapitationGrant> findByGrantNumberAndIsActiveTrue(String grantNumber);
    
    @Query("SELECT c FROM CapitationGrant c WHERE c.school.id = :schoolId AND c.academicYear.id = :academicYearId AND c.isActive = true ORDER BY c.applicationDate DESC")
    List<CapitationGrant> findBySchoolIdAndAcademicYearIdAndIsActiveTrueOrderByApplicationDateDesc(
        @Param("schoolId") Long schoolId,
        @Param("academicYearId") Long academicYearId
    );
    
    @Query("SELECT SUM(c.totalAmount) FROM CapitationGrant c WHERE c.school.id = :schoolId AND c.academicYear.id = :academicYearId AND c.isActive = true")
    Double getTotalApprovedAmountBySchoolAndAcademicYear(@Param("schoolId") Long schoolId, @Param("academicYearId") Long academicYearId);
    
    @Query("SELECT SUM(c.receivedAmount) FROM CapitationGrant c WHERE c.school.id = :schoolId AND c.academicYear.id = :academicYearId AND c.isActive = true")
    Double getTotalDisbursedAmountBySchoolAndAcademicYear(@Param("schoolId") Long schoolId, @Param("academicYearId") Long academicYearId);
    
    List<CapitationGrant> findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(Long schoolId);
}