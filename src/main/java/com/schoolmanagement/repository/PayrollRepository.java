package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    
    List<Payroll> findByStaffIdAndIsActiveTrueOrderByPayDateDesc(Long staffId);
    
    List<Payroll> findByPayPeriodStartBetweenAndIsActiveTrueOrderByPayDateDesc(LocalDate startDate, LocalDate endDate);
    
    List<Payroll> findByStatusAndIsActiveTrueOrderByPayDateDesc(Payroll.PayrollStatus status);
    
    @Query("SELECT p FROM Payroll p WHERE p.staff.id = :staffId AND p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate AND p.isActive = true ORDER BY p.payDate DESC")
    List<Payroll> findByStaffIdAndPayPeriodBetweenAndIsActiveTrueOrderByPayDateDesc(
        @Param("staffId") Long staffId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT p FROM Payroll p WHERE p.status = :status AND p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate AND p.isActive = true ORDER BY p.payDate DESC")
    List<Payroll> findByStatusAndPayPeriodBetweenAndIsActiveTrueOrderByPayDateDesc(
        @Param("status") Payroll.PayrollStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT SUM(p.grossSalary) FROM Payroll p WHERE p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate AND p.isActive = true")
    Double getTotalGrossSalaryByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate AND p.isActive = true")
    Double getTotalNetSalaryByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate AND p.isActive = true")
    Long countByPayPeriodBetweenAndIsActiveTrue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

