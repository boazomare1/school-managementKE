package com.schoolmanagement.repository;

import com.schoolmanagement.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    
    List<PerformanceReview> findBySupportStaffIdAndIsActiveTrueOrderByReviewDateDesc(Long supportStaffId);
    
    List<PerformanceReview> findByReviewerIdAndIsActiveTrueOrderByReviewDateDesc(Long reviewerId);
    
    List<PerformanceReview> findByStatusAndIsActiveTrueOrderByReviewDateDesc(PerformanceReview.ReviewStatus status);
    
    @Query("SELECT p FROM PerformanceReview p WHERE p.supportStaff.id = :supportStaffId AND p.reviewPeriodStart >= :startDate AND p.reviewPeriodEnd <= :endDate AND p.isActive = true ORDER BY p.reviewDate DESC")
    List<PerformanceReview> findBySupportStaffIdAndReviewPeriodBetweenAndIsActiveTrueOrderByReviewDateDesc(
        @Param("supportStaffId") Long supportStaffId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT AVG(p.overallRating) FROM PerformanceReview p WHERE p.supportStaff.id = :supportStaffId AND p.isActive = true")
    Double getAverageRatingBySupportStaffId(@Param("supportStaffId") Long supportStaffId);
    
    @Query("SELECT COUNT(p) FROM PerformanceReview p WHERE p.supportStaff.id = :supportStaffId AND p.isActive = true")
    Long countBySupportStaffIdAndIsActiveTrue(@Param("supportStaffId") Long supportStaffId);
}

