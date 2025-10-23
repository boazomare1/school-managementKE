package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.FeeStructureDto;
import com.schoolmanagement.dto.FeeInvoiceDto;
import com.schoolmanagement.dto.PaymentDto;
import com.schoolmanagement.dto.PaymentRequestDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FinanceService {
    
    private final FeeStructureRepository feeStructureRepository;
    private final FeeInvoiceRepository feeInvoiceRepository;
    private final PaymentRepository paymentRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final SchoolRepository schoolRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ClassRepository classRepository;
    
    // Fee Structure Management
    public ApiResponse<FeeStructureDto> createFeeStructure(FeeStructureDto feeStructureDto) {
        try {
            log.info("Creating fee structure: {}", feeStructureDto.getName());
            
            // Validate school exists
            Optional<School> school = schoolRepository.findById(feeStructureDto.getSchoolId());
            if (school.isEmpty()) {
                return ApiResponse.error("School not found");
            }
            
            // Validate academic year exists
            Optional<AcademicYear> academicYear = academicYearRepository.findById(feeStructureDto.getAcademicYearId());
            if (academicYear.isEmpty()) {
                return ApiResponse.error("Academic year not found");
            }
            
            // Check if fee structure with same name already exists
            Optional<FeeStructure> existingFeeStructure = feeStructureRepository.findBySchoolIdAndNameAndIsActiveTrue(
                    feeStructureDto.getSchoolId(), feeStructureDto.getName());
            if (existingFeeStructure.isPresent()) {
                return ApiResponse.error("Fee structure with name '" + feeStructureDto.getName() + "' already exists");
            }
            
            FeeStructure feeStructure = new FeeStructure();
            feeStructure.setName(feeStructureDto.getName());
            feeStructure.setDescription(feeStructureDto.getDescription());
            feeStructure.setAmount(feeStructureDto.getAmount());
            feeStructure.setFeeType(feeStructureDto.getFeeType());
            feeStructure.setPaymentFrequency(feeStructureDto.getPaymentFrequency());
            feeStructure.setIsMandatory(feeStructureDto.getIsMandatory() != null ? feeStructureDto.getIsMandatory() : true);
            feeStructure.setIsActive(feeStructureDto.getIsActive() != null ? feeStructureDto.getIsActive() : true);
            feeStructure.setEffectiveFrom(feeStructureDto.getEffectiveFrom());
            feeStructure.setEffectiveTo(feeStructureDto.getEffectiveTo());
            feeStructure.setSchool(school.get());
            feeStructure.setAcademicYear(academicYear.get());
            
            // Set class if provided
            if (feeStructureDto.getClassId() != null) {
                Optional<ClassEntity> classEntity = classRepository.findById(feeStructureDto.getClassId());
                if (classEntity.isPresent()) {
                    feeStructure.setClassEntity(classEntity.get());
                }
            }
            
            FeeStructure savedFeeStructure = feeStructureRepository.save(feeStructure);
            log.info("Fee structure created successfully: {}", savedFeeStructure.getId());
            
            return ApiResponse.success("Fee structure created successfully", convertToDto(savedFeeStructure));
            
        } catch (Exception e) {
            log.error("Error creating fee structure: {}", e.getMessage());
            return ApiResponse.error("Failed to create fee structure: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<FeeStructureDto>> getAllFeeStructures(Long schoolId, Long academicYearId) {
        try {
            log.info("Fetching fee structures for school: {} and academic year: {}", schoolId, academicYearId);
            List<FeeStructure> feeStructures = feeStructureRepository.findBySchoolIdAndAcademicYearIdAndIsActiveTrue(schoolId, academicYearId);
            List<FeeStructureDto> feeStructureDtos = feeStructures.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            return ApiResponse.success("Fee structures retrieved successfully", feeStructureDtos);
            
        } catch (Exception e) {
            log.error("Error fetching fee structures: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve fee structures: " + e.getMessage());
        }
    }
    
    // Fee Invoice Management
    public ApiResponse<FeeInvoiceDto> createFeeInvoice(Long enrollmentId, Long feeStructureId) {
        try {
            log.info("Creating fee invoice for enrollment: {} and fee structure: {}", enrollmentId, feeStructureId);
            
            // Validate enrollment exists
            Optional<StudentEnrollment> enrollment = studentEnrollmentRepository.findById(enrollmentId);
            if (enrollment.isEmpty()) {
                return ApiResponse.error("Student enrollment not found");
            }
            
            // Validate fee structure exists
            Optional<FeeStructure> feeStructure = feeStructureRepository.findById(feeStructureId);
            if (feeStructure.isEmpty()) {
                return ApiResponse.error("Fee structure not found");
            }
            
            // Generate invoice number
            String invoiceNumber = "INV-" + System.currentTimeMillis();
            
            FeeInvoice invoice = new FeeInvoice();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setIssueDate(LocalDate.now());
            invoice.setDueDate(LocalDate.now().plusDays(30)); // 30 days from issue date
            invoice.setTotalAmount(feeStructure.get().getAmount());
            invoice.setPaidAmount(BigDecimal.ZERO);
            invoice.setBalanceAmount(feeStructure.get().getAmount());
            invoice.setStatus("PENDING");
            invoice.setEnrollment(enrollment.get());
            invoice.setFeeStructure(feeStructure.get());
            
            FeeInvoice savedInvoice = feeInvoiceRepository.save(invoice);
            log.info("Fee invoice created successfully: {}", savedInvoice.getId());
            
            return ApiResponse.success("Fee invoice created successfully", convertToDto(savedInvoice));
            
        } catch (Exception e) {
            log.error("Error creating fee invoice: {}", e.getMessage());
            return ApiResponse.error("Failed to create fee invoice: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<FeeInvoiceDto>> getStudentInvoices(Long enrollmentId) {
        try {
            log.info("Fetching invoices for enrollment: {}", enrollmentId);
            List<FeeInvoice> invoices = feeInvoiceRepository.findActiveInvoicesByEnrollment(enrollmentId);
            List<FeeInvoiceDto> invoiceDtos = invoices.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            return ApiResponse.success("Student invoices retrieved successfully", invoiceDtos);
            
        } catch (Exception e) {
            log.error("Error fetching student invoices: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve student invoices: " + e.getMessage());
        }
    }
    
    // Payment Processing
    public ApiResponse<PaymentDto> processPayment(PaymentRequestDto paymentRequest, User processedBy) {
        try {
            log.info("Processing payment for invoice: {} with amount: {}", paymentRequest.getInvoiceId(), paymentRequest.getAmount());
            
            // Validate invoice exists
            Optional<FeeInvoice> invoice = feeInvoiceRepository.findById(paymentRequest.getInvoiceId());
            if (invoice.isEmpty()) {
                return ApiResponse.error("Invoice not found");
            }
            
            // Check if payment amount is valid
            if (paymentRequest.getAmount().compareTo(invoice.get().getBalanceAmount()) > 0) {
                return ApiResponse.error("Payment amount cannot exceed balance amount");
            }
            
            // Generate payment reference
            String paymentReference = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            Payment payment = new Payment();
            payment.setPaymentReference(paymentReference);
            payment.setAmount(paymentRequest.getAmount());
            payment.setPaymentMethod(paymentRequest.getPaymentMethod());
            payment.setPaymentStatus("PENDING");
            payment.setTransactionId(paymentRequest.getTransactionId());
            payment.setExternalReference(paymentRequest.getExternalReference());
            payment.setPaymentNotes(paymentRequest.getPaymentNotes());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setInvoice(invoice.get());
            payment.setEnrollment(invoice.get().getEnrollment());
            payment.setProcessedBy(processedBy);
            
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment created successfully: {}", savedPayment.getId());
            
            // Update invoice status
            updateInvoiceStatus(invoice.get(), paymentRequest.getAmount());
            
            return ApiResponse.success("Payment processed successfully", convertToDto(savedPayment));
            
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage());
            return ApiResponse.error("Failed to process payment: " + e.getMessage());
        }
    }
    
    private void updateInvoiceStatus(FeeInvoice invoice, BigDecimal paymentAmount) {
        BigDecimal newPaidAmount = invoice.getPaidAmount().add(paymentAmount);
        invoice.setPaidAmount(newPaidAmount);
        invoice.setBalanceAmount(invoice.getTotalAmount().subtract(newPaidAmount));
        
        if (invoice.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIAL");
        }
        
        feeInvoiceRepository.save(invoice);
    }
    
    // DTO Conversion Methods
    private FeeStructureDto convertToDto(FeeStructure feeStructure) {
        FeeStructureDto dto = new FeeStructureDto();
        dto.setId(feeStructure.getId());
        dto.setName(feeStructure.getName());
        dto.setDescription(feeStructure.getDescription());
        dto.setAmount(feeStructure.getAmount());
        dto.setFeeType(feeStructure.getFeeType());
        dto.setPaymentFrequency(feeStructure.getPaymentFrequency());
        dto.setIsMandatory(feeStructure.getIsMandatory());
        dto.setIsActive(feeStructure.getIsActive());
        dto.setEffectiveFrom(feeStructure.getEffectiveFrom());
        dto.setEffectiveTo(feeStructure.getEffectiveTo());
        dto.setSchoolId(feeStructure.getSchool().getId());
        dto.setSchoolName(feeStructure.getSchool().getName());
        dto.setAcademicYearId(feeStructure.getAcademicYear().getId());
        dto.setAcademicYearName(feeStructure.getAcademicYear().getName());
        
        if (feeStructure.getClassEntity() != null) {
            dto.setClassId(feeStructure.getClassEntity().getId());
            dto.setClassName(feeStructure.getClassEntity().getName());
        }
        
        return dto;
    }
    
    private FeeInvoiceDto convertToDto(FeeInvoice invoice) {
        FeeInvoiceDto dto = new FeeInvoiceDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setPaidAmount(invoice.getPaidAmount());
        dto.setBalanceAmount(invoice.getBalanceAmount());
        dto.setStatus(invoice.getStatus());
        dto.setNotes(invoice.getNotes());
        dto.setIsActive(invoice.getIsActive());
        dto.setEnrollmentId(invoice.getEnrollment().getId());
        dto.setStudentName(invoice.getEnrollment().getStudent().getFirstName() + " " + invoice.getEnrollment().getStudent().getLastName());
        dto.setStudentEmail(invoice.getEnrollment().getStudent().getEmail());
        dto.setFeeStructureId(invoice.getFeeStructure().getId());
        dto.setFeeStructureName(invoice.getFeeStructure().getName());
        dto.setClassName(invoice.getEnrollment().getClassEntity().getName());
        dto.setAcademicYearName(invoice.getEnrollment().getClassEntity().getAcademicYear().getName());
        return dto;
    }
    
    private PaymentDto convertToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setPaymentReference(payment.getPaymentReference());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setExternalReference(payment.getExternalReference());
        dto.setPaymentNotes(payment.getPaymentNotes());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setIsActive(payment.getIsActive());
        dto.setInvoiceId(payment.getInvoice().getId());
        dto.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
        dto.setEnrollmentId(payment.getEnrollment().getId());
        dto.setStudentName(payment.getEnrollment().getStudent().getFirstName() + " " + payment.getEnrollment().getStudent().getLastName());
        dto.setStudentEmail(payment.getEnrollment().getStudent().getEmail());
        
        if (payment.getProcessedBy() != null) {
            dto.setProcessedById(payment.getProcessedBy().getId());
            dto.setProcessedByName(payment.getProcessedBy().getFirstName() + " " + payment.getProcessedBy().getLastName());
        }
        
        return dto;
    }
}


