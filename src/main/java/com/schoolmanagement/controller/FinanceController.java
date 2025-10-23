package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.FeeStructureDto;
import com.schoolmanagement.dto.FeeInvoiceDto;
import com.schoolmanagement.dto.PaymentDto;
import com.schoolmanagement.dto.PaymentRequestDto;
import com.schoolmanagement.service.FinanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Slf4j
public class FinanceController {
    
    private final FinanceService financeService;
    
    // Fee Structure Management
    @PostMapping("/fee-structures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeeStructureDto>> createFeeStructure(@Valid @RequestBody FeeStructureDto feeStructureDto) {
        log.info("Creating fee structure request: {}", feeStructureDto.getName());
        return ResponseEntity.ok(financeService.createFeeStructure(feeStructureDto));
    }
    
    @GetMapping("/fee-structures")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<FeeStructureDto>>> getAllFeeStructures(
            @RequestParam Long schoolId, 
            @RequestParam Long academicYearId) {
        log.info("Get all fee structures request for school: {} and academic year: {}", schoolId, academicYearId);
        return ResponseEntity.ok(financeService.getAllFeeStructures(schoolId, academicYearId));
    }
    
    // Fee Invoice Management
    @PostMapping("/invoices")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeeInvoiceDto>> createFeeInvoice(
            @RequestParam Long enrollmentId, 
            @RequestParam Long feeStructureId) {
        log.info("Creating fee invoice request for enrollment: {} and fee structure: {}", enrollmentId, feeStructureId);
        return ResponseEntity.ok(financeService.createFeeInvoice(enrollmentId, feeStructureId));
    }
    
    @GetMapping("/invoices/student/{enrollmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<FeeInvoiceDto>>> getStudentInvoices(@PathVariable Long enrollmentId) {
        log.info("Get student invoices request for enrollment: {}", enrollmentId);
        return ResponseEntity.ok(financeService.getStudentInvoices(enrollmentId));
    }
    
    // Payment Processing
    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(
            @Valid @RequestBody PaymentRequestDto paymentRequest,
            Authentication authentication) {
        log.info("Processing payment request for invoice: {}", paymentRequest.getInvoiceId());
        
        // Get current user from authentication
        com.schoolmanagement.entity.User currentUser = (com.schoolmanagement.entity.User) authentication.getPrincipal();
        
        return ResponseEntity.ok(financeService.processPayment(paymentRequest, currentUser));
    }
}


