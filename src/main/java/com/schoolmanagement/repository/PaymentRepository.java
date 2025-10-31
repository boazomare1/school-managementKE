package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentReference(String paymentReference);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByEnrollmentIdAndIsActiveTrue(Long enrollmentId);
    
    List<Payment> findByPaymentStatusAndIsActiveTrue(String paymentStatus);
    
    @Query("SELECT p FROM Payment p WHERE p.enrollment.id = :enrollmentId AND p.isActive = true ORDER BY p.paymentDate DESC")
    List<Payment> findActivePaymentsByEnrollment(Long enrollmentId);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.isActive = true ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.enrollment.id = :enrollmentId AND p.paymentStatus = 'COMPLETED' AND p.isActive = true")
    java.math.BigDecimal getTotalPaidAmountByEnrollment(Long enrollmentId);
    
    Optional<Payment> findByExternalReference(String externalReference);
}


