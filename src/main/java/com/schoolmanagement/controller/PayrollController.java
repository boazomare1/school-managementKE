package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.PayrollDto;
import com.schoolmanagement.entity.Payroll;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.PayrollRepository;
import com.schoolmanagement.repository.StaffRepository;
import com.schoolmanagement.service.PayrollService;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Slf4j
public class PayrollController {

    private final PayrollService payrollService;
    private final PayrollRepository payrollRepository;
    private final StaffRepository staffRepository;

    @PostMapping("/process/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'FINANCE')")
    public ResponseEntity<ApiResponse<PayrollDto>> processPayroll(
            @PathVariable Long staffId,
            @RequestParam LocalDate payPeriodStart,
            @RequestParam LocalDate payPeriodEnd,
            Authentication authentication) {
        try {
            log.info("Processing payroll for staff: {} from {} to {}", staffId, payPeriodStart, payPeriodEnd);
            ApiResponse<Payroll> response = payrollService.processPayroll(staffId, payPeriodStart, payPeriodEnd);
            
            if (response.isSuccess()) {
                PayrollDto payrollDto = convertToDto(response.getData());
                return ResponseEntity.ok(ApiResponse.success("Payroll processed successfully", payrollDto));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error processing payroll: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to process payroll: " + e.getMessage()));
        }
    }

    @PutMapping("/approve/{payrollId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'FINANCE')")
    public ResponseEntity<ApiResponse<PayrollDto>> approvePayroll(
            @PathVariable Long payrollId,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            log.info("Approving payroll: {} by user: {}", payrollId, user.getUsername());
            ApiResponse<Payroll> response = payrollService.approvePayroll(payrollId, user);
            
            if (response.isSuccess()) {
                PayrollDto payrollDto = convertToDto(response.getData());
                return ResponseEntity.ok(ApiResponse.success("Payroll approved successfully", payrollDto));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error approving payroll: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to approve payroll: " + e.getMessage()));
        }
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'FINANCE')")
    public ResponseEntity<ApiResponse<List<PayrollDto>>> getStaffPayrollHistory(@PathVariable Long staffId) {
        try {
            log.info("Fetching payroll history for staff: {}", staffId);
            ApiResponse<List<Payroll>> response = payrollService.getStaffPayrollHistory(staffId);
            
            if (response.isSuccess()) {
                List<PayrollDto> payrollDtos = response.getData().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(ApiResponse.success("Payroll history retrieved successfully", payrollDtos));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error fetching payroll history: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch payroll history: " + e.getMessage()));
        }
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'FINANCE')")
    public ResponseEntity<ApiResponse<List<PayrollDto>>> getMonthlyPayroll(@RequestParam LocalDate month) {
        try {
            log.info("Fetching monthly payroll for: {}", month);
            ApiResponse<List<Payroll>> response = payrollService.getMonthlyPayroll(month);
            
            if (response.isSuccess()) {
                List<PayrollDto> payrollDtos = response.getData().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(ApiResponse.success("Monthly payroll retrieved successfully", payrollDtos));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error fetching monthly payroll: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch monthly payroll: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'FINANCE')")
    public ResponseEntity<ApiResponse<Object>> getPayrollSummary(@RequestParam LocalDate month) {
        try {
            log.info("Fetching payroll summary for: {}", month);
            ApiResponse<Object> response = payrollService.getPayrollSummary(month);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching payroll summary: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch payroll summary: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'FINANCE')")
    public ResponseEntity<ApiResponse<PayrollDto>> getPayrollById(@PathVariable Long id) {
        try {
            log.info("Fetching payroll with ID: {}", id);
            Optional<Payroll> payrollOptional = payrollRepository.findById(id);
            if (payrollOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            PayrollDto payrollDto = convertToDto(payrollOptional.get());
            return ResponseEntity.ok(ApiResponse.success("Payroll retrieved successfully", payrollDto));
        } catch (Exception e) {
            log.error("Error fetching payroll by ID: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch payroll: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'FINANCE')")
    public ResponseEntity<ApiResponse<List<PayrollDto>>> getPayrollByStatus(@PathVariable Payroll.PayrollStatus status) {
        try {
            log.info("Fetching payroll by status: {}", status);
            List<Payroll> payrolls = payrollRepository.findByStatusAndIsActiveTrueOrderByPayDateDesc(status);
            List<PayrollDto> payrollDtos = payrolls.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Payroll retrieved successfully", payrollDtos));
        } catch (Exception e) {
            log.error("Error fetching payroll by status: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch payroll: " + e.getMessage()));
        }
    }

    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'FINANCE')")
    public ResponseEntity<ApiResponse<List<PayrollDto>>> getPayrollByPeriod(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            log.info("Fetching payroll for period: {} to {}", startDate, endDate);
            List<Payroll> payrolls = payrollRepository.findByPayPeriodStartBetweenAndIsActiveTrueOrderByPayDateDesc(startDate, endDate);
            List<PayrollDto> payrollDtos = payrolls.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Payroll retrieved successfully", payrollDtos));
        } catch (Exception e) {
            log.error("Error fetching payroll by period: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch payroll: " + e.getMessage()));
        }
    }

    private PayrollDto convertToDto(Payroll payroll) {
        return PayrollDto.builder()
                .id(payroll.getId())
                .staffId(payroll.getStaff().getId())
                .staffName(payroll.getStaff().getUser().getFirstName() + " " + payroll.getStaff().getUser().getLastName())
                .employeeNumber(payroll.getStaff().getEmployeeNumber())
                .payPeriodStart(payroll.getPayPeriodStart())
                .payPeriodEnd(payroll.getPayPeriodEnd())
                .payDate(payroll.getPayDate())
                .basicSalary(payroll.getBasicSalary())
                .houseAllowance(payroll.getHouseAllowance())
                .transportAllowance(payroll.getTransportAllowance())
                .medicalAllowance(payroll.getMedicalAllowance())
                .otherAllowances(payroll.getOtherAllowances())
                .grossSalary(payroll.getGrossSalary())
                .nhifDeduction(payroll.getNhifDeduction())
                .nssfDeduction(payroll.getNssfDeduction())
                .payeDeduction(payroll.getPayeDeduction())
                .otherDeductions(payroll.getOtherDeductions())
                .totalDeductions(payroll.getTotalDeductions())
                .netSalary(payroll.getNetSalary())
                .status(payroll.getStatus())
                .notes(payroll.getNotes())
                .isActive(payroll.getIsActive())
                .createdAt(payroll.getCreatedAt())
                .updatedAt(payroll.getUpdatedAt())
                .build();
    }
}
