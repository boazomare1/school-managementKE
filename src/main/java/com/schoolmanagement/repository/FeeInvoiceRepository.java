package com.schoolmanagement.repository;

import com.schoolmanagement.entity.FeeInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeInvoiceRepository extends JpaRepository<FeeInvoice, Long> {
    
    Optional<FeeInvoice> findByInvoiceNumber(String invoiceNumber);
    
    List<FeeInvoice> findByEnrollmentIdAndIsActiveTrue(Long enrollmentId);
    
    List<FeeInvoice> findByEnrollmentIdAndStatusAndIsActiveTrue(Long enrollmentId, String status);
    
    @Query("SELECT fi FROM FeeInvoice fi WHERE fi.enrollment.id = :enrollmentId AND fi.isActive = true ORDER BY fi.issueDate DESC")
    List<FeeInvoice> findActiveInvoicesByEnrollment(Long enrollmentId);
    
    @Query("SELECT fi FROM FeeInvoice fi WHERE fi.status = :status AND fi.isActive = true ORDER BY fi.dueDate")
    List<FeeInvoice> findInvoicesByStatus(String status);
    
    @Query("SELECT fi FROM FeeInvoice fi WHERE fi.dueDate < :currentDate AND fi.status IN ('PENDING', 'PARTIAL') AND fi.isActive = true ORDER BY fi.dueDate")
    List<FeeInvoice> findOverdueInvoices(LocalDate currentDate);
}


