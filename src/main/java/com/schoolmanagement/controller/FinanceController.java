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
import java.math.BigDecimal;
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
            @RequestParam(required = false) Long enrollmentId,
            @RequestParam(required = false) Long studentId,
            @RequestParam Long feeStructureId) {
        log.info("Creating fee invoice request for enrollment: {}, student: {}, fee structure: {}", enrollmentId, studentId, feeStructureId);
        
        // If studentId is provided, use it; otherwise use enrollmentId
        if (studentId != null) {
            return ResponseEntity.ok(financeService.createFeeInvoiceByStudentId(studentId, feeStructureId));
        } else if (enrollmentId != null) {
            return ResponseEntity.ok(financeService.createFeeInvoice(enrollmentId, feeStructureId));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Either enrollmentId or studentId must be provided"));
        }
    }
    
    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<FeeInvoiceDto>>> getAllInvoices(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String status) {
        log.info("Get all invoices request - school: {}, status: {}", schoolId, status);
        return ResponseEntity.ok(financeService.getAllInvoices(schoolId, status));
    }
    
    @GetMapping("/invoices/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<FeeInvoiceDto>>> getStudentInvoicesByStudentId(@PathVariable Long studentId) {
        log.info("Get student invoices request for student ID: {}", studentId);
        return ResponseEntity.ok(financeService.getStudentInvoicesByStudentId(studentId));
    }
    
    @GetMapping("/invoices/enrollment/{enrollmentId}")
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
    
    // Enhanced Fee Structure Management
    @GetMapping("/fee-structures/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<FeeStructureDto>> getFeeStructureById(@PathVariable Long id) {
        log.info("Get fee structure by ID: {}", id);
        return ResponseEntity.ok(financeService.getFeeStructureById(id));
    }
    
    @PutMapping("/fee-structures/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeeStructureDto>> updateFeeStructure(
            @PathVariable Long id, 
            @Valid @RequestBody FeeStructureDto feeStructureDto) {
        log.info("Update fee structure request for ID: {}", id);
        return ResponseEntity.ok(financeService.updateFeeStructure(id, feeStructureDto));
    }
    
    @DeleteMapping("/fee-structures/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteFeeStructure(@PathVariable Long id) {
        log.info("Delete fee structure request for ID: {}", id);
        return ResponseEntity.ok(financeService.deleteFeeStructure(id));
    }
    
    // Enhanced Invoice Management
    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<FeeInvoiceDto>> getInvoiceById(@PathVariable Long id) {
        log.info("Get invoice by ID: {}", id);
        return ResponseEntity.ok(financeService.getInvoiceById(id));
    }
    
    @PutMapping("/invoices/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeeInvoiceDto>> updateInvoice(
            @PathVariable Long id, 
            @Valid @RequestBody FeeInvoiceDto invoiceDto) {
        log.info("Update invoice request for ID: {}", id);
        return ResponseEntity.ok(financeService.updateInvoice(id, invoiceDto));
    }
    
    @PostMapping("/invoices/{id}/send-reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<String>> sendPaymentReminder(@PathVariable Long id) {
        log.info("Send payment reminder for invoice ID: {}", id);
        return ResponseEntity.ok(financeService.sendPaymentReminder(id));
    }
    
    // Payment Management
    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(@PathVariable Long id) {
        log.info("Get payment by ID: {}", id);
        return ResponseEntity.ok(financeService.getPaymentById(id));
    }
    
    @GetMapping("/payments/student/{enrollmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getStudentPayments(@PathVariable Long enrollmentId) {
        log.info("Get student payments for enrollment: {}", enrollmentId);
        return ResponseEntity.ok(financeService.getStudentPayments(enrollmentId));
    }
    
    @PostMapping("/payments/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentDto>> processRefund(
            @PathVariable Long id, 
            @RequestParam BigDecimal refundAmount,
            @RequestParam String refundReason) {
        log.info("Process refund for payment ID: {} with amount: {}", id, refundAmount);
        return ResponseEntity.ok(financeService.processRefund(id, refundAmount, refundReason));
    }
    
    // Finance Dashboard and Reports
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Object>> getFinanceDashboard(
            @RequestParam Long schoolId,
            @RequestParam Long academicYearId) {
        log.info("Get finance dashboard for school: {} and academic year: {}", schoolId, academicYearId);
        return ResponseEntity.ok(financeService.getFinanceDashboard(schoolId, academicYearId));
    }
    
    @GetMapping("/reports/fee-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Object>> getFeeSummaryReport(
            @RequestParam Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam(required = false) Long classId) {
        log.info("Get fee summary report for school: {}, academic year: {}, class: {}", schoolId, academicYearId, classId);
        return ResponseEntity.ok(financeService.getFeeSummaryReport(schoolId, academicYearId, classId));
    }
    
    @GetMapping("/reports/payment-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Object>> getPaymentSummaryReport(
            @RequestParam Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Get payment summary report for school: {}, academic year: {}", schoolId, academicYearId);
        return ResponseEntity.ok(financeService.getPaymentSummaryReport(schoolId, academicYearId, startDate, endDate));
    }
    
    @GetMapping("/reports/overdue-fees")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<FeeInvoiceDto>>> getOverdueFees(
            @RequestParam Long schoolId,
            @RequestParam Long academicYearId) {
        log.info("Get overdue fees for school: {} and academic year: {}", schoolId, academicYearId);
        return ResponseEntity.ok(financeService.getOverdueFees(schoolId, academicYearId));
    }
    
    // Kenya-specific Fee Management
    @PostMapping("/kenya-fee-structures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> createKenyaFeeStructure(@Valid @RequestBody com.schoolmanagement.dto.KenyaFeeStructureDto kenyaFeeStructureDto) {
        log.info("Creating Kenya fee structure: {}", kenyaFeeStructureDto.getFeeName());
        return ResponseEntity.ok(financeService.createKenyaFeeStructure(kenyaFeeStructureDto));
    }
    
    @GetMapping("/kenya-fee-structures")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<com.schoolmanagement.dto.KenyaFeeStructureDto>>> getKenyaFeeStructures(
            @RequestParam Long schoolId,
            @RequestParam Long academicYearId) {
        log.info("Get Kenya fee structures for school: {} and academic year: {}", schoolId, academicYearId);
        return ResponseEntity.ok(financeService.getKenyaFeeStructures(schoolId, academicYearId));
    }
    
    // Payment Gateway Integration
    @PostMapping("/payments/mpesa/stk-push")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<com.schoolmanagement.dto.MpesaStkPushResponse>> initiateMpesaStkPush(
            @Valid @RequestBody PaymentRequestDto paymentRequest,
            Authentication authentication) {
        log.info("Initiating M-Pesa STK Push for invoice: {}", paymentRequest.getInvoiceId());
        com.schoolmanagement.entity.User currentUser = (com.schoolmanagement.entity.User) authentication.getPrincipal();
        return ResponseEntity.ok(financeService.initiateMpesaStkPush(paymentRequest, currentUser));
    }
    
    @PostMapping("/payments/stripe/create-payment-intent")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Object>> createStripePaymentIntent(
            @Valid @RequestBody PaymentRequestDto paymentRequest,
            Authentication authentication) {
        log.info("Creating Stripe payment intent for invoice: {}", paymentRequest.getInvoiceId());
        com.schoolmanagement.entity.User currentUser = (com.schoolmanagement.entity.User) authentication.getPrincipal();
        return ResponseEntity.ok(financeService.createStripePaymentIntent(paymentRequest, currentUser));
    }
    
    @PostMapping("/payments/webhooks/mpesa")
    public ResponseEntity<String> handleMpesaWebhook(@RequestBody String webhookPayload) {
        log.info("Received M-Pesa webhook");
        return ResponseEntity.ok(financeService.handleMpesaWebhook(webhookPayload));
    }
    
    @PostMapping("/payments/webhooks/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String webhookPayload) {
        log.info("Received Stripe webhook");
        return ResponseEntity.ok(financeService.handleStripeWebhook(webhookPayload));
    }
}


