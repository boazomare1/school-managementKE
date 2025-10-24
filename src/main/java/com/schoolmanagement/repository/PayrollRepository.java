package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Payroll;
import com.schoolmanagement.entity.SupportStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    
    List<Payroll> findBySupportStaff(SupportStaff supportStaff);
    
    List<Payroll> findBySupportStaffAndIsActiveTrue(SupportStaff supportStaff);
    
    List<Payroll> findByPayPeriodStartBetween(LocalDate startDate, LocalDate endDate);
    
    List<Payroll> findByStatusAndIsActiveTrue(Payroll.PayrollStatus status);
    
    @Query("SELECT p FROM Payroll p WHERE p.supportStaff = :staff AND p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate")
    List<Payroll> findByStaffAndDateRange(@Param("staff") SupportStaff staff, 
                                         @Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM Payroll p WHERE p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate AND p.isActive = true")
    List<Payroll> findByDateRange(@Param("startDate") LocalDate startDate, 
                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(p.netPay) FROM Payroll p WHERE p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate AND p.status = 'PAID'")
    Double getTotalPayrollForPeriod(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.payPeriodStart >= :startDate AND p.payPeriodEnd <= :endDate AND p.status = 'PAID'")
    Long getPaidStaffCountForPeriod(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
}