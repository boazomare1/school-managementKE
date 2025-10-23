package com.schoolmanagement.repository;

import com.schoolmanagement.entity.AdmissionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {
    
    List<AdmissionApplication> findByStatusAndIsActiveTrueOrderByApplicationDateDesc(AdmissionApplication.ApplicationStatus status);
    
    List<AdmissionApplication> findByApplyingClassIdAndIsActiveTrueOrderByApplicationDateDesc(Long classId);
    
    List<AdmissionApplication> findByAcademicYearIdAndIsActiveTrueOrderByApplicationDateDesc(Long academicYearId);
    
    List<AdmissionApplication> findByApplicationDateBetweenAndIsActiveTrueOrderByApplicationDateDesc(LocalDate startDate, LocalDate endDate);
    
    Optional<AdmissionApplication> findByApplicationNumberAndIsActiveTrue(String applicationNumber);
    
    @Query("SELECT a FROM AdmissionApplication a WHERE a.status = :status AND a.applyingClass.id = :classId AND a.isActive = true ORDER BY a.applicationDate DESC")
    List<AdmissionApplication> findByStatusAndApplyingClassIdAndIsActiveTrueOrderByApplicationDateDesc(
        @Param("status") AdmissionApplication.ApplicationStatus status,
        @Param("classId") Long classId
    );
    
    @Query("SELECT a FROM AdmissionApplication a WHERE a.kcpeMarks >= :minMarks AND a.isActive = true ORDER BY a.kcpeMarks DESC")
    List<AdmissionApplication> findByKcpeMarksGreaterThanEqualAndIsActiveTrueOrderByKcpeMarksDesc(@Param("minMarks") Double minMarks);
    
    @Query("SELECT COUNT(a) FROM AdmissionApplication a WHERE a.status = :status AND a.isActive = true")
    Long countByStatusAndIsActiveTrue(@Param("status") AdmissionApplication.ApplicationStatus status);
    
    @Query("SELECT COUNT(a) FROM AdmissionApplication a WHERE a.applyingClass.id = :classId AND a.isActive = true")
    Long countByApplyingClassIdAndIsActiveTrue(@Param("classId") Long classId);
    
    @Query("SELECT a FROM AdmissionApplication a WHERE a.applicationDate >= :startDate AND a.applicationDate <= :endDate AND a.isActive = true ORDER BY a.applicationDate DESC")
    List<AdmissionApplication> findByApplicationDateRangeAndIsActiveTrueOrderByApplicationDateDesc(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
