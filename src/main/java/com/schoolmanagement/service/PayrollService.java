package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.Payroll;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.PayrollRepository;
import com.schoolmanagement.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class PayrollSummary {
    public long totalStaff;
    public long pendingCount;
    public long processedCount;
    public long paidCount;
    public String month;
}

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final StaffRepository staffRepository;

    @Transactional
    public ApiResponse<Payroll> processPayroll(Long staffId, LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        try {
            log.info("Processing payroll for staff: {} from {} to {}", staffId, payPeriodStart, payPeriodEnd);
            
            // Check if staff exists
            if (!staffRepository.existsById(staffId)) {
                return ApiResponse.error("Staff not found");
            }

            // Create payroll record (simplified for now)
            Payroll payroll = Payroll.builder()
                    .staff(staffRepository.findById(staffId).get())
                    .payPeriodStart(payPeriodStart)
                    .payPeriodEnd(payPeriodEnd)
                    .payDate(LocalDate.now())
                    .status(Payroll.PayrollStatus.PENDING)
                    .isActive(true)
                    .build();

            Payroll savedPayroll = payrollRepository.save(payroll);
            return ApiResponse.success("Payroll processed successfully", savedPayroll);

        } catch (Exception e) {
            log.error("Error processing payroll: {}", e.getMessage());
            return ApiResponse.error("Failed to process payroll: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Payroll> approvePayroll(Long payrollId, User approver) {
        try {
            log.info("Approving payroll: {} by user: {}", payrollId, approver.getUsername());
            
            Optional<Payroll> payrollOptional = payrollRepository.findById(payrollId);
            if (payrollOptional.isEmpty()) {
                return ApiResponse.error("Payroll not found");
            }

            Payroll payroll = payrollOptional.get();
            payroll.setStatus(Payroll.PayrollStatus.PROCESSED);
            Payroll savedPayroll = payrollRepository.save(payroll);

            return ApiResponse.success("Payroll approved successfully", savedPayroll);

        } catch (Exception e) {
            log.error("Error approving payroll: {}", e.getMessage());
            return ApiResponse.error("Failed to approve payroll: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<Payroll>> getStaffPayrollHistory(Long staffId) {
        try {
            log.info("Fetching payroll history for staff: {}", staffId);
            List<Payroll> payrolls = payrollRepository.findByStaffIdAndIsActiveTrueOrderByPayDateDesc(staffId);
            return ApiResponse.success("Payroll history retrieved successfully", payrolls);
        } catch (Exception e) {
            log.error("Error fetching payroll history: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch payroll history: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<Payroll>> getMonthlyPayroll(LocalDate month) {
        try {
            log.info("Fetching monthly payroll for: {}", month);
            List<Payroll> payrolls = payrollRepository.findByPayPeriodStartBetweenAndIsActiveTrueOrderByPayDateDesc(
                    month.withDayOfMonth(1), month.withDayOfMonth(month.lengthOfMonth()));
            return ApiResponse.success("Monthly payroll retrieved successfully", payrolls);
        } catch (Exception e) {
            log.error("Error fetching monthly payroll: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch monthly payroll: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Object> getPayrollSummary(LocalDate month) {
        try {
            log.info("Fetching payroll summary for: {}", month);
            
            List<Payroll> payrolls = payrollRepository.findByPayPeriodStartBetweenAndIsActiveTrueOrderByPayDateDesc(
                    month.withDayOfMonth(1), month.withDayOfMonth(month.lengthOfMonth()));
            
            // Calculate summary statistics
            long totalStaff = payrolls.size();
            long pendingCount = payrolls.stream().filter(p -> p.getStatus() == Payroll.PayrollStatus.PENDING).count();
            long processedCount = payrolls.stream().filter(p -> p.getStatus() == Payroll.PayrollStatus.PROCESSED).count();
            long paidCount = payrolls.stream().filter(p -> p.getStatus() == Payroll.PayrollStatus.PAID).count();
            
            PayrollSummary summary = new PayrollSummary();
            summary.totalStaff = totalStaff;
            summary.pendingCount = pendingCount;
            summary.processedCount = processedCount;
            summary.paidCount = paidCount;
            summary.month = month.toString();
            
            return ApiResponse.success("Payroll summary retrieved successfully", summary);
        } catch (Exception e) {
            log.error("Error fetching payroll summary: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch payroll summary: " + e.getMessage());
        }
    }
}