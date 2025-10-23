package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findBySchoolIdAndIsActiveTrue(Long schoolId);
    
    List<Report> findBySchoolIdAndReportTypeAndIsActiveTrue(Long schoolId, String reportType);
    
    List<Report> findBySchoolIdAndStatusAndIsActiveTrue(Long schoolId, String status);
    
    @Query("SELECT r FROM Report r WHERE r.school.id = :schoolId AND r.isActive = true ORDER BY r.generatedAt DESC")
    List<Report> findRecentReportsBySchool(Long schoolId);
    
    @Query("SELECT r FROM Report r WHERE r.school.id = :schoolId AND r.reportType = :reportType AND r.isActive = true ORDER BY r.generatedAt DESC")
    List<Report> findReportsBySchoolAndType(Long schoolId, String reportType);
    
    @Query("SELECT r FROM Report r WHERE r.generatedAt BETWEEN :startDate AND :endDate AND r.isActive = true ORDER BY r.generatedAt DESC")
    List<Report> findReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}


