package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.PayrollDto;
import com.schoolmanagement.dto.PayrollAllowanceDto;
import com.schoolmanagement.dto.PayrollDeductionDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PayrollService {
    
    private final PayrollRepository payrollRepository;
    private final SupportStaffRepository supportStaffRepository;
    private final NotificationService notificationService;
    private final PayrollAllowanceRepository payrollAllowanceRepository;
    private final PayrollDeductionRepository payrollDeductionRepository;
    
    // Process payroll for a single staff member
    public ApiResponse<PayrollDto> processPayroll(Long staffId, LocalDate payPeriodStart, LocalDate payPeriodEnd, 
                                                 List<PayrollAllowanceDto> allowances, List<PayrollDeductionDto> deductions) {
        try {
            log.info("Processing payroll for staff ID: {} for period {} to {}", staffId, payPeriodStart, payPeriodEnd);
            
            SupportStaff supportStaff = supportStaffRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Support staff not found"));
            
            // Calculate payroll
            BigDecimal basicSalary = supportStaff.getBasicSalary();
            BigDecimal overtimePay = BigDecimal.ZERO;
            BigDecimal totalAllowances = allowances.stream()
                    .map(PayrollAllowanceDto::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal bonuses = BigDecimal.ZERO;
            
            BigDecimal grossPay = basicSalary.add(overtimePay).add(totalAllowances).add(bonuses);
            
            // Calculate deductions
            BigDecimal taxDeduction = calculateTax(grossPay);
            BigDecimal socialSecurityDeduction = calculateSocialSecurity(grossPay);
            BigDecimal otherDeductions = deductions.stream()
                    .map(PayrollDeductionDto::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalDeductions = taxDeduction.add(socialSecurityDeduction).add(otherDeductions);
            BigDecimal netPay = grossPay.subtract(totalDeductions);
            
            // Create payroll record
            Payroll payroll = Payroll.builder()
                    .supportStaff(supportStaff)
                    .payPeriodStart(payPeriodStart)
                    .payPeriodEnd(payPeriodEnd)
                    .payDate(LocalDate.now())
                    .basicSalary(basicSalary)
                    .overtimePay(overtimePay)
                    .totalAllowances(totalAllowances)
                    .bonuses(bonuses)
                    .grossPay(grossPay)
                    .taxDeduction(taxDeduction)
                    .socialSecurityDeduction(socialSecurityDeduction)
                    .otherDeductions(otherDeductions)
                    .totalDeductions(totalDeductions)
                    .netPay(netPay)
                    .status(Payroll.PayrollStatus.DRAFT)
                    .paymentMethod("Bank Transfer")
                    .isActive(true)
                    .build();
            
            Payroll savedPayroll = payrollRepository.save(payroll);
            
            // Save allowances
            for (PayrollAllowanceDto allowanceDto : allowances) {
                PayrollAllowance allowance = PayrollAllowance.builder()
                        .payroll(savedPayroll)
                        .allowanceType(allowanceDto.getAllowanceType())
                        .description(allowanceDto.getDescription())
                        .amount(allowanceDto.getAmount())
                        .isTaxable(allowanceDto.getIsTaxable())
                        .reference(allowanceDto.getReference())
                        .build();
                // Note: You would need a PayrollAllowanceRepository to save this
            }
            
            // Save deductions
            for (PayrollDeductionDto deductionDto : deductions) {
                PayrollDeduction deduction = PayrollDeduction.builder()
                        .payroll(savedPayroll)
                        .deductionType(deductionDto.getDeductionType())
                        .description(deductionDto.getDescription())
                        .amount(deductionDto.getAmount())
                        .isMandatory(deductionDto.getIsMandatory())
                        .reference(deductionDto.getReference())
                        .build();
                // Note: You would need a PayrollDeductionRepository to save this
            }
            
            log.info("Successfully processed payroll with ID: {}", savedPayroll.getId());
            return ApiResponse.success("Payroll processed successfully", convertToDto(savedPayroll));
            
        } catch (Exception e) {
            log.error("Error processing payroll: {}", e.getMessage());
            return ApiResponse.error("Failed to process payroll: " + e.getMessage());
        }
    }
    
    // Process payroll for all active staff
    public ApiResponse<List<PayrollDto>> processBulkPayroll(LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        try {
            log.info("Processing bulk payroll for period {} to {}", payPeriodStart, payPeriodEnd);
            
            List<SupportStaff> activeStaff = supportStaffRepository.findActiveStaff();
            List<PayrollDto> processedPayrolls = activeStaff.stream()
                    .map(staff -> {
                        try {
                            ApiResponse<PayrollDto> result = processPayroll(staff.getId(), payPeriodStart, payPeriodEnd, 
                                    List.of(), List.of());
                            return result.getData();
                        } catch (Exception e) {
                            log.error("Error processing payroll for staff {}: {}", staff.getId(), e.getMessage());
                            return null;
                        }
                    })
                    .filter(payroll -> payroll != null)
                    .toList();
            
            log.info("Successfully processed {} payroll records", processedPayrolls.size());
            return ApiResponse.success("Bulk payroll processed successfully", processedPayrolls);
            
        } catch (Exception e) {
            log.error("Error processing bulk payroll: {}", e.getMessage());
            return ApiResponse.error("Failed to process bulk payroll: " + e.getMessage());
        }
    }
    
    // Get payroll by ID
    @Transactional(readOnly = true)
    public ApiResponse<PayrollDto> getPayrollById(Long id) {
        try {
            log.info("Fetching payroll by ID: {}", id);
            Payroll payroll = payrollRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payroll not found"));
            return ApiResponse.success("Payroll retrieved successfully", convertToDto(payroll));
        } catch (Exception e) {
            log.error("Error fetching payroll by ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to fetch payroll: " + e.getMessage());
        }
    }
    
    // Get payrolls for a staff member
    @Transactional(readOnly = true)
    public ApiResponse<List<PayrollDto>> getStaffPayrolls(Long staffId, int page, int size) {
        try {
            log.info("Fetching payrolls for staff ID: {}", staffId);
            SupportStaff supportStaff = supportStaffRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Support staff not found"));
            
            Pageable pageable = PageRequest.of(page, size);
            List<Payroll> payrolls = payrollRepository.findBySupportStaffAndIsActiveTrue(supportStaff);
            
            List<PayrollDto> payrollDtos = payrolls.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Staff payrolls retrieved successfully", payrollDtos);
        } catch (Exception e) {
            log.error("Error fetching staff payrolls: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch staff payrolls: " + e.getMessage());
        }
    }
    
    // Get payrolls for a period
    @Transactional(readOnly = true)
    public ApiResponse<List<PayrollDto>> getPayrollsForPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Fetching payrolls for period {} to {}", startDate, endDate);
            List<Payroll> payrolls = payrollRepository.findByDateRange(startDate, endDate);
            
            List<PayrollDto> payrollDtos = payrolls.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Payrolls for period retrieved successfully", payrollDtos);
        } catch (Exception e) {
            log.error("Error fetching payrolls for period: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch payrolls for period: " + e.getMessage());
        }
    }
    
    // Approve payroll
    public ApiResponse<PayrollDto> approvePayroll(Long id) {
        try {
            log.info("Approving payroll with ID: {}", id);
            Payroll payroll = payrollRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payroll not found"));
            
            payroll.setStatus(Payroll.PayrollStatus.APPROVED);
            Payroll updatedPayroll = payrollRepository.save(payroll);
            
            // Send approval notification
            try {
                sendPayrollApprovalNotification(updatedPayroll);
            } catch (Exception e) {
                log.error("Error sending payroll approval notification: {}", e.getMessage());
            }
            
            log.info("Successfully approved payroll with ID: {}", updatedPayroll.getId());
            return ApiResponse.success("Payroll approved successfully", convertToDto(updatedPayroll));
            
        } catch (Exception e) {
            log.error("Error approving payroll: {}", e.getMessage());
            return ApiResponse.error("Failed to approve payroll: " + e.getMessage());
        }
    }
    
    // Process payment
    public ApiResponse<PayrollDto> processPayment(Long id, String paymentMethod, String transactionReference) {
        try {
            log.info("Processing payment for payroll ID: {}", id);
            Payroll payroll = payrollRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payroll not found"));
            
            payroll.setStatus(Payroll.PayrollStatus.PAID);
            payroll.setPaymentMethod(paymentMethod);
            payroll.setTransactionReference(transactionReference);
            Payroll updatedPayroll = payrollRepository.save(payroll);
            
            // Send payment notification
            try {
                sendPayrollPaymentNotification(updatedPayroll);
            } catch (Exception e) {
                log.error("Error sending payroll payment notification: {}", e.getMessage());
            }
            
            log.info("Successfully processed payment for payroll ID: {}", updatedPayroll.getId());
            return ApiResponse.success("Payment processed successfully", convertToDto(updatedPayroll));
            
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage());
            return ApiResponse.error("Failed to process payment: " + e.getMessage());
        }
    }
    
    // Get payroll statistics
    @Transactional(readOnly = true)
    public ApiResponse<Object> getPayrollStatistics(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Fetching payroll statistics for period {} to {}", startDate, endDate);
            
            Double totalPayroll = payrollRepository.getTotalPayrollForPeriod(startDate, endDate);
            Long paidStaffCount = payrollRepository.getPaidStaffCountForPeriod(startDate, endDate);
            
            Double finalTotalPayroll = totalPayroll != null ? totalPayroll : 0.0;
            Long finalPaidStaffCount = paidStaffCount != null ? paidStaffCount : 0L;
            
            Object statistics = new Object() {
                public final Double totalPayroll = finalTotalPayroll;
                public final Long paidStaffCount = finalPaidStaffCount;
                public final Double averagePay = finalPaidStaffCount > 0 ? finalTotalPayroll / finalPaidStaffCount : 0.0;
            };
            
            return ApiResponse.success("Payroll statistics retrieved successfully", statistics);
            
        } catch (Exception e) {
            log.error("Error fetching payroll statistics: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch payroll statistics: " + e.getMessage());
        }
    }
    
    // Create payroll record
    public ApiResponse<PayrollDto> createPayroll(PayrollDto payrollDto) {
        try {
            log.info("Creating payroll for staff ID: {}", payrollDto.getSupportStaffId());
            
            SupportStaff supportStaff = supportStaffRepository.findById(payrollDto.getSupportStaffId())
                    .orElseThrow(() -> new RuntimeException("Support staff not found"));
            
            Payroll payroll = Payroll.builder()
                    .supportStaff(supportStaff)
                    .payPeriodStart(payrollDto.getPayPeriodStart())
                    .payPeriodEnd(payrollDto.getPayPeriodEnd())
                    .payDate(payrollDto.getPayDate())
                    .basicSalary(payrollDto.getBasicSalary())
                    .overtimePay(payrollDto.getOvertimePay() != null ? payrollDto.getOvertimePay() : BigDecimal.ZERO)
                    .totalAllowances(payrollDto.getTotalAllowances() != null ? payrollDto.getTotalAllowances() : BigDecimal.ZERO)
                    .bonuses(payrollDto.getBonuses() != null ? payrollDto.getBonuses() : BigDecimal.ZERO)
                    .grossPay(payrollDto.getGrossPay())
                    .taxDeduction(BigDecimal.ZERO)
                    .socialSecurityDeduction(BigDecimal.ZERO)
                    .otherDeductions(BigDecimal.ZERO)
                    .totalDeductions(payrollDto.getTotalDeductions())
                    .netPay(payrollDto.getNetPay())
                    .status(payrollDto.getStatus() != null ? payrollDto.getStatus() : Payroll.PayrollStatus.DRAFT)
                    .paymentMethod(payrollDto.getPaymentMethod())
                    .transactionReference(payrollDto.getTransactionReference())
                    .notes(payrollDto.getNotes())
                    .isActive(true)
                    .build();
            
            Payroll savedPayroll = payrollRepository.save(payroll);
            log.info("Successfully created payroll with ID: {}", savedPayroll.getId());
            return ApiResponse.success("Payroll created successfully", convertToDto(savedPayroll));
            
        } catch (Exception e) {
            log.error("Error creating payroll: {}", e.getMessage());
            return ApiResponse.error("Failed to create payroll: " + e.getMessage());
        }
    }
    
    // Add allowance to payroll
    public ApiResponse<PayrollAllowanceDto> addAllowance(PayrollAllowanceDto allowanceDto) {
        try {
            log.info("Adding allowance to payroll ID: {}", allowanceDto.getPayrollId());
            
            Payroll payroll = payrollRepository.findById(allowanceDto.getPayrollId())
                    .orElseThrow(() -> new RuntimeException("Payroll not found"));
            
            PayrollAllowance allowance = PayrollAllowance.builder()
                    .payroll(payroll)
                    .allowanceType(allowanceDto.getAllowanceType())
                    .description(allowanceDto.getDescription())
                    .amount(allowanceDto.getAmount())
                    .isTaxable(allowanceDto.getIsTaxable() != null ? allowanceDto.getIsTaxable() : true)
                    .reference(allowanceDto.getReference())
                    .build();
            
            PayrollAllowance savedAllowance = payrollAllowanceRepository.save(allowance);
            
            // Update payroll totals
            BigDecimal currentAllowances = payroll.getTotalAllowances() != null ? payroll.getTotalAllowances() : BigDecimal.ZERO;
            payroll.setTotalAllowances(currentAllowances.add(allowanceDto.getAmount()));
            payroll.setGrossPay(payroll.getBasicSalary().add(payroll.getTotalAllowances()));
            payroll.setNetPay(payroll.getGrossPay().subtract(payroll.getTotalDeductions()));
            payrollRepository.save(payroll);
            
            PayrollAllowanceDto responseDto = PayrollAllowanceDto.builder()
                    .id(savedAllowance.getId())
                    .payrollId(savedAllowance.getPayroll().getId())
                    .allowanceType(savedAllowance.getAllowanceType())
                    .description(savedAllowance.getDescription())
                    .amount(savedAllowance.getAmount())
                    .isTaxable(savedAllowance.getIsTaxable())
                    .reference(savedAllowance.getReference())
                    .build();
            
            log.info("Successfully added allowance to payroll ID: {}", allowanceDto.getPayrollId());
            return ApiResponse.success("Allowance added successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error adding allowance: {}", e.getMessage());
            return ApiResponse.error("Failed to add allowance: " + e.getMessage());
        }
    }
    
    // Add deduction to payroll
    public ApiResponse<PayrollDeductionDto> addDeduction(PayrollDeductionDto deductionDto) {
        try {
            log.info("Adding deduction to payroll ID: {}", deductionDto.getPayrollId());
            
            Payroll payroll = payrollRepository.findById(deductionDto.getPayrollId())
                    .orElseThrow(() -> new RuntimeException("Payroll not found"));
            
            PayrollDeduction deduction = PayrollDeduction.builder()
                    .payroll(payroll)
                    .deductionType(deductionDto.getDeductionType())
                    .description(deductionDto.getDescription())
                    .amount(deductionDto.getAmount())
                    .isMandatory(deductionDto.getIsMandatory() != null ? deductionDto.getIsMandatory() : false)
                    .reference(deductionDto.getReference())
                    .build();
            
            PayrollDeduction savedDeduction = payrollDeductionRepository.save(deduction);
            
            // Update payroll totals
            BigDecimal currentDeductions = payroll.getTotalDeductions() != null ? payroll.getTotalDeductions() : BigDecimal.ZERO;
            payroll.setTotalDeductions(currentDeductions.add(deductionDto.getAmount()));
            payroll.setNetPay(payroll.getGrossPay().subtract(payroll.getTotalDeductions()));
            payrollRepository.save(payroll);
            
            PayrollDeductionDto responseDto = PayrollDeductionDto.builder()
                    .id(savedDeduction.getId())
                    .payrollId(savedDeduction.getPayroll().getId())
                    .deductionType(savedDeduction.getDeductionType())
                    .description(savedDeduction.getDescription())
                    .amount(savedDeduction.getAmount())
                    .isMandatory(savedDeduction.getIsMandatory())
                    .reference(savedDeduction.getReference())
                    .build();
            
            log.info("Successfully added deduction to payroll ID: {}", deductionDto.getPayrollId());
            return ApiResponse.success("Deduction added successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error adding deduction: {}", e.getMessage());
            return ApiResponse.error("Failed to add deduction: " + e.getMessage());
        }
    }
    
    // Calculate tax (simplified calculation)
    private BigDecimal calculateTax(BigDecimal grossPay) {
        // Simplified tax calculation - in real implementation, this would be more complex
        if (grossPay.compareTo(new BigDecimal("10000")) <= 0) {
            return BigDecimal.ZERO;
        } else if (grossPay.compareTo(new BigDecimal("25000")) <= 0) {
            return grossPay.multiply(new BigDecimal("0.1")); // 10%
        } else {
            return grossPay.multiply(new BigDecimal("0.15")); // 15%
        }
    }
    
    // Calculate social security (simplified calculation)
    private BigDecimal calculateSocialSecurity(BigDecimal grossPay) {
        // Simplified social security calculation
        return grossPay.multiply(new BigDecimal("0.05")); // 5%
    }
    
    // Send payroll approval notification
    private void sendPayrollApprovalNotification(Payroll payroll) {
        try {
            String message = String.format(
                "Your payroll for the period %s to %s has been approved. " +
                "Net pay: KES %s. Payment will be processed shortly.",
                payroll.getPayPeriodStart(),
                payroll.getPayPeriodEnd(),
                payroll.getNetPay()
            );
            
            com.schoolmanagement.dto.NotificationRequestDto notificationRequest = 
                com.schoolmanagement.dto.NotificationRequestDto.builder()
                    .title("Payroll Approved")
                    .message(message)
                    .type(Notification.NotificationType.PAYMENT)
                    .priority(Notification.NotificationPriority.MEDIUM)
                    .recipientId(payroll.getSupportStaff().getUser().getId())
                    .actionUrl("/staff/payroll")
                    .actionText("View Payroll Details")
                    .build();
            
            notificationService.createNotification(notificationRequest);
            log.info("Payroll approval notification sent to: {}", payroll.getSupportStaff().getUser().getUsername());
            
        } catch (Exception e) {
            log.error("Error sending payroll approval notification: {}", e.getMessage());
        }
    }
    
    // Send payroll payment notification
    private void sendPayrollPaymentNotification(Payroll payroll) {
        try {
            String message = String.format(
                "Your salary payment of KES %s has been processed successfully. " +
                "Payment method: %s. Transaction reference: %s",
                payroll.getNetPay(),
                payroll.getPaymentMethod(),
                payroll.getTransactionReference()
            );
            
            com.schoolmanagement.dto.NotificationRequestDto notificationRequest = 
                com.schoolmanagement.dto.NotificationRequestDto.builder()
                    .title("Salary Payment Processed")
                    .message(message)
                    .type(Notification.NotificationType.PAYMENT)
                    .priority(Notification.NotificationPriority.HIGH)
                    .recipientId(payroll.getSupportStaff().getUser().getId())
                    .actionUrl("/staff/payroll")
                    .actionText("View Payment Details")
                    .build();
            
            notificationService.createNotification(notificationRequest);
            log.info("Payroll payment notification sent to: {}", payroll.getSupportStaff().getUser().getUsername());
            
        } catch (Exception e) {
            log.error("Error sending payroll payment notification: {}", e.getMessage());
        }
    }
    
    // Convert entity to DTO
    private PayrollDto convertToDto(Payroll payroll) {
        try {
            SupportStaff staff = payroll.getSupportStaff();
            return PayrollDto.builder()
                    .id(payroll.getId())
                    .supportStaffId(staff != null ? staff.getId() : null)
                    .payPeriodStart(payroll.getPayPeriodStart())
                    .payPeriodEnd(payroll.getPayPeriodEnd())
                    .payDate(payroll.getPayDate())
                    .basicSalary(payroll.getBasicSalary())
                    .overtimePay(payroll.getOvertimePay())
                    .totalAllowances(payroll.getTotalAllowances())
                    .bonuses(payroll.getBonuses())
                    .grossPay(payroll.getGrossPay())
                    .taxDeduction(payroll.getTaxDeduction())
                    .socialSecurityDeduction(payroll.getSocialSecurityDeduction())
                    .otherDeductions(payroll.getOtherDeductions())
                    .totalDeductions(payroll.getTotalDeductions())
                    .netPay(payroll.getNetPay())
                    .status(payroll.getStatus())
                    .paymentMethod(payroll.getPaymentMethod())
                    .transactionReference(payroll.getTransactionReference())
                    .notes(payroll.getNotes())
                    .isActive(payroll.getIsActive())
                    .createdAt(payroll.getCreatedAt())
                    .updatedAt(payroll.getUpdatedAt())
                    .employeeId(staff != null ? staff.getEmployeeId() : null)
                    .staffName(staff != null && staff.getUser() != null ? 
                            staff.getUser().getFirstName() + " " + staff.getUser().getLastName() : null)
                    .department(staff != null ? staff.getDepartment() : null)
                    .position(staff != null ? staff.getPosition() : null)
                    .build();
        } catch (Exception e) {
            log.error("Error converting payroll to DTO: {}", e.getMessage());
            return PayrollDto.builder()
                    .id(payroll.getId())
                    .payPeriodStart(payroll.getPayPeriodStart())
                    .payPeriodEnd(payroll.getPayPeriodEnd())
                    .payDate(payroll.getPayDate())
                    .basicSalary(payroll.getBasicSalary())
                    .netPay(payroll.getNetPay())
                    .status(payroll.getStatus())
                    .createdAt(payroll.getCreatedAt())
                    .build();
        }
    }
}