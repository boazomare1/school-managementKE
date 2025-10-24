package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.PayrollDto;
import com.schoolmanagement.dto.PayrollAllowanceDto;
import com.schoolmanagement.dto.PayrollDeductionDto;
import com.schoolmanagement.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Slf4j
public class PayrollController {
    
    private final PayrollService payrollService;
    
    // Process payroll for a single staff member
    @PostMapping("/process/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PayrollDto>> processPayroll(
            @PathVariable Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payPeriodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payPeriodEnd,
            @RequestBody(required = false) List<PayrollAllowanceDto> allowances,
            @RequestBody(required = false) List<PayrollDeductionDto> deductions) {
        log.info("Processing payroll for staff ID: {} for period {} to {}", staffId, payPeriodStart, payPeriodEnd);
        return ResponseEntity.ok(payrollService.processPayroll(staffId, payPeriodStart, payPeriodEnd, 
                allowances != null ? allowances : List.of(), 
                deductions != null ? deductions : List.of()));
    }
    
    // Process bulk payroll for all active staff
    @PostMapping("/process-bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<PayrollDto>>> processBulkPayroll(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payPeriodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payPeriodEnd) {
        log.info("Processing bulk payroll for period {} to {}", payPeriodStart, payPeriodEnd);
        return ResponseEntity.ok(payrollService.processBulkPayroll(payPeriodStart, payPeriodEnd));
    }
    
    // Get payroll by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<PayrollDto>> getPayrollById(@PathVariable Long id) {
        log.info("Get payroll by ID: {}", id);
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }
    
    // Get payrolls for a staff member
    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<PayrollDto>>> getStaffPayrolls(
            @PathVariable Long staffId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get payrolls for staff ID: {}", staffId);
        return ResponseEntity.ok(payrollService.getStaffPayrolls(staffId, page, size));
    }
    
    // Get payrolls for a period
    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<PayrollDto>>> getPayrollsForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Get payrolls for period {} to {}", startDate, endDate);
        return ResponseEntity.ok(payrollService.getPayrollsForPeriod(startDate, endDate));
    }
    
    // Approve payroll
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PayrollDto>> approvePayroll(@PathVariable Long id) {
        log.info("Approve payroll with ID: {}", id);
        return ResponseEntity.ok(payrollService.approvePayroll(id));
    }
    
    // Process payment
    @PostMapping("/{id}/process-payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PayrollDto>> processPayment(
            @PathVariable Long id,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String transactionReference) {
        log.info("Process payment for payroll ID: {}", id);
        return ResponseEntity.ok(payrollService.processPayment(id, paymentMethod, transactionReference));
    }
    
    // Get payroll statistics
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<Object>> getPayrollStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Get payroll statistics for period {} to {}", startDate, endDate);
        return ResponseEntity.ok(payrollService.getPayrollStatistics(startDate, endDate));
    }
    
    // Create payroll record
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PayrollDto>> createPayroll(@Valid @RequestBody PayrollDto payrollDto) {
        log.info("Creating payroll for staff ID: {}", payrollDto.getSupportStaffId());
        return ResponseEntity.ok(payrollService.createPayroll(payrollDto));
    }
    
    // Add allowance to payroll
    @PostMapping("/allowances")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PayrollAllowanceDto>> addAllowance(@Valid @RequestBody PayrollAllowanceDto allowanceDto) {
        log.info("Adding allowance to payroll ID: {}", allowanceDto.getPayrollId());
        return ResponseEntity.ok(payrollService.addAllowance(allowanceDto));
    }
    
    // Add deduction to payroll
    @PostMapping("/deductions")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PayrollDeductionDto>> addDeduction(@Valid @RequestBody PayrollDeductionDto deductionDto) {
        log.info("Adding deduction to payroll ID: {}", deductionDto.getPayrollId());
        return ResponseEntity.ok(payrollService.addDeduction(deductionDto));
    }
}