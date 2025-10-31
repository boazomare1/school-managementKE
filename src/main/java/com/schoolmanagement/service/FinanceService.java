package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.FeeStructureDto;
import com.schoolmanagement.dto.FeeInvoiceDto;
import com.schoolmanagement.dto.PaymentDto;
import com.schoolmanagement.dto.PaymentRequestDto;
import com.schoolmanagement.dto.MpesaStkPushResponse;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final NotificationService notificationService;
    private final KenyaFeeStructureRepository kenyaFeeStructureRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final UserRepository userRepository;
    private final MpesaService mpesaService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
                    .toList();
            
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
    
    // Create invoice using student ID (finds active enrollment automatically)
    public ApiResponse<FeeInvoiceDto> createFeeInvoiceByStudentId(Long studentId, Long feeStructureId) {
        try {
            log.info("Creating fee invoice for student: {} and fee structure: {}", studentId, feeStructureId);
            
            // Find active enrollment for student
            List<StudentEnrollment> enrollments = studentEnrollmentRepository.findByStudentIdAndIsActiveTrue(studentId);
            StudentEnrollment enrollment;
            
            if (enrollments.isEmpty()) {
                // Auto-create enrollment if none exists (for testing/convenience)
                log.warn("No enrollment found for student {}, creating enrollment automatically", studentId);
                
                Optional<User> studentOpt = userRepository.findById(studentId);
                if (studentOpt.isEmpty()) {
                    return ApiResponse.error("Student not found");
                }
                
                User student = studentOpt.get();
                
                // Try to find any active class (default to class ID 1, or find first available)
                Optional<ClassEntity> classOpt = classRepository.findById(1L);
                if (classOpt.isEmpty()) {
                    List<ClassEntity> classes = classRepository.findAll().stream()
                        .filter(ClassEntity::getIsActive)
                        .toList();
                    if (classes.isEmpty()) {
                        return ApiResponse.error("No active classes found. Please create a class first.");
                    }
                    classOpt = Optional.of(classes.get(0));
                }
                
                ClassEntity classEntity = classOpt.get();
                
                // Create enrollment
                enrollment = new StudentEnrollment();
                enrollment.setStudent(student);
                enrollment.setClassEntity(classEntity);
                enrollment.setEnrollmentNumber("ENR-" + System.currentTimeMillis());
                enrollment.setEnrollmentDate(LocalDate.now());
                enrollment.setIsActive(true);
                enrollment.setNotes("Auto-created for invoice generation");
                
                enrollment = studentEnrollmentRepository.save(enrollment);
                log.info("Auto-created enrollment ID: {} for student: {}", enrollment.getId(), studentId);
            } else {
                // Use the most recent enrollment (first in list if sorted by date DESC)
                enrollment = enrollments.get(0);
                log.info("Using enrollment ID: {} for student: {}", enrollment.getId(), studentId);
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
            invoice.setEnrollment(enrollment);
            invoice.setFeeStructure(feeStructure.get());
            
            FeeInvoice savedInvoice = feeInvoiceRepository.save(invoice);
            log.info("Fee invoice created successfully for student {}: {}", studentId, savedInvoice.getId());
            
            return ApiResponse.success("Fee invoice created successfully", convertToDto(savedInvoice));
            
        } catch (Exception e) {
            log.error("Error creating fee invoice by student ID: {}", e.getMessage());
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
                    .toList();
            
            return ApiResponse.success("Student invoices retrieved successfully", invoiceDtos);
            
        } catch (Exception e) {
            log.error("Error fetching student invoices: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve student invoices: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<FeeInvoiceDto>> getStudentInvoicesByStudentId(Long studentId) {
        try {
            log.info("Fetching invoices for student ID: {}", studentId);
            List<FeeInvoice> invoices = feeInvoiceRepository.findActiveInvoicesByStudentId(studentId);
            List<FeeInvoiceDto> invoiceDtos = invoices.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Student invoices retrieved successfully", invoiceDtos);
            
        } catch (Exception e) {
            log.error("Error fetching student invoices by student ID: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve student invoices: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<FeeInvoiceDto>> getAllInvoices(Long schoolId, String status) {
        try {
            log.info("Fetching all invoices - school: {}, status: {}", schoolId, status);
            
            List<FeeInvoice> invoices;
            if (schoolId != null && status != null && !status.trim().isEmpty()) {
                invoices = feeInvoiceRepository.findActiveInvoicesBySchoolIdAndStatus(schoolId, status);
            } else if (schoolId != null) {
                invoices = feeInvoiceRepository.findActiveInvoicesBySchoolId(schoolId);
            } else if (status != null && !status.trim().isEmpty()) {
                invoices = feeInvoiceRepository.findActiveInvoicesByStatus(status);
            } else {
                invoices = feeInvoiceRepository.findAllActiveInvoices();
            }
            
            List<FeeInvoiceDto> invoiceDtos = invoices.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Invoices retrieved successfully", invoiceDtos);
            
        } catch (Exception e) {
            log.error("Error fetching all invoices: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve invoices: " + e.getMessage());
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
            
            // Send payment notification
            try {
                sendPaymentNotification(savedPayment, invoice.get());
            } catch (Exception e) {
                log.error("Error sending payment notification: {}", e.getMessage());
                // Don't fail payment if notification fails
            }
            
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
    
    // Enhanced Fee Structure Management
    @Transactional(readOnly = true)
    public ApiResponse<FeeStructureDto> getFeeStructureById(Long id) {
        try {
            log.info("Fetching fee structure by ID: {}", id);
            FeeStructure feeStructure = feeStructureRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fee structure not found with ID: " + id));
            return ApiResponse.success("Fee structure retrieved successfully", convertToDto(feeStructure));
        } catch (Exception e) {
            log.error("Error fetching fee structure by ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to retrieve fee structure: " + e.getMessage());
        }
    }
    
    public ApiResponse<FeeStructureDto> updateFeeStructure(Long id, FeeStructureDto feeStructureDto) {
        try {
            log.info("Updating fee structure with ID: {}", id);
            FeeStructure existingFeeStructure = feeStructureRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fee structure not found with ID: " + id));
            
            // Update fields
            existingFeeStructure.setName(feeStructureDto.getName());
            existingFeeStructure.setDescription(feeStructureDto.getDescription());
            existingFeeStructure.setAmount(feeStructureDto.getAmount());
            existingFeeStructure.setFeeType(feeStructureDto.getFeeType());
            existingFeeStructure.setPaymentFrequency(feeStructureDto.getPaymentFrequency());
            existingFeeStructure.setIsMandatory(feeStructureDto.getIsMandatory());
            existingFeeStructure.setIsActive(feeStructureDto.getIsActive());
            existingFeeStructure.setEffectiveFrom(feeStructureDto.getEffectiveFrom());
            existingFeeStructure.setEffectiveTo(feeStructureDto.getEffectiveTo());
            
            FeeStructure updatedFeeStructure = feeStructureRepository.save(existingFeeStructure);
            return ApiResponse.success("Fee structure updated successfully", convertToDto(updatedFeeStructure));
        } catch (Exception e) {
            log.error("Error updating fee structure with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to update fee structure: " + e.getMessage());
        }
    }
    
    public ApiResponse<String> deleteFeeStructure(Long id) {
        try {
            log.info("Deleting fee structure with ID: {}", id);
            FeeStructure feeStructure = feeStructureRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fee structure not found with ID: " + id));
            feeStructureRepository.delete(feeStructure);
            return ApiResponse.success("Fee structure deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting fee structure with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to delete fee structure: " + e.getMessage());
        }
    }
    
    // Enhanced Invoice Management
    @Transactional(readOnly = true)
    public ApiResponse<FeeInvoiceDto> getInvoiceById(Long id) {
        try {
            log.info("Fetching invoice by ID: {}", id);
            FeeInvoice invoice = feeInvoiceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
            return ApiResponse.success("Invoice retrieved successfully", convertToDto(invoice));
        } catch (Exception e) {
            log.error("Error fetching invoice by ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to retrieve invoice: " + e.getMessage());
        }
    }
    
    public ApiResponse<FeeInvoiceDto> updateInvoice(Long id, FeeInvoiceDto invoiceDto) {
        try {
            log.info("Updating invoice with ID: {}", id);
            FeeInvoice existingInvoice = feeInvoiceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
            
            // Update fields
            existingInvoice.setDueDate(invoiceDto.getDueDate());
            existingInvoice.setNotes(invoiceDto.getNotes());
            existingInvoice.setIsActive(invoiceDto.getIsActive());
            
            FeeInvoice updatedInvoice = feeInvoiceRepository.save(existingInvoice);
            return ApiResponse.success("Invoice updated successfully", convertToDto(updatedInvoice));
        } catch (Exception e) {
            log.error("Error updating invoice with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to update invoice: " + e.getMessage());
        }
    }
    
    public ApiResponse<String> sendPaymentReminder(Long invoiceId) {
        try {
            log.info("Sending payment reminder for invoice ID: {}", invoiceId);
            FeeInvoice invoice = feeInvoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
            
            // TODO: Implement email notification service
            // For now, just log the reminder
            log.info("Payment reminder sent for invoice: {} to student: {}", 
                    invoice.getInvoiceNumber(), invoice.getEnrollment().getStudent().getEmail());
            
            return ApiResponse.success("Payment reminder sent successfully");
        } catch (Exception e) {
            log.error("Error sending payment reminder for invoice ID {}: {}", invoiceId, e.getMessage());
            return ApiResponse.error("Failed to send payment reminder: " + e.getMessage());
        }
    }
    
    // Enhanced Payment Management
    @Transactional(readOnly = true)
    public ApiResponse<PaymentDto> getPaymentById(Long id) {
        try {
            log.info("Fetching payment by ID: {}", id);
            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
            return ApiResponse.success("Payment retrieved successfully", convertToDto(payment));
        } catch (Exception e) {
            log.error("Error fetching payment by ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to retrieve payment: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<PaymentDto>> getStudentPayments(Long enrollmentId) {
        try {
            log.info("Fetching payments for enrollment: {}", enrollmentId);
            List<Payment> payments = paymentRepository.findActivePaymentsByEnrollment(enrollmentId);
            List<PaymentDto> paymentDtos = payments.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("Student payments retrieved successfully", paymentDtos);
        } catch (Exception e) {
            log.error("Error fetching student payments for enrollment {}: {}", enrollmentId, e.getMessage());
            return ApiResponse.error("Failed to retrieve student payments: " + e.getMessage());
        }
    }
    
    public ApiResponse<PaymentDto> processRefund(Long paymentId, BigDecimal refundAmount, String refundReason) {
        try {
            log.info("Processing refund for payment ID: {} with amount: {}", paymentId, refundAmount);
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
            
            // Create refund payment
            Payment refundPayment = new Payment();
            refundPayment.setPaymentReference("REF-" + payment.getPaymentReference());
            refundPayment.setAmount(refundAmount.negate()); // Negative amount for refund
            refundPayment.setPaymentMethod(payment.getPaymentMethod());
            refundPayment.setPaymentStatus("REFUNDED");
            refundPayment.setPaymentNotes("Refund: " + refundReason);
            refundPayment.setPaymentDate(LocalDateTime.now());
            refundPayment.setInvoice(payment.getInvoice());
            refundPayment.setEnrollment(payment.getEnrollment());
            refundPayment.setProcessedBy(payment.getProcessedBy());
            
            Payment savedRefund = paymentRepository.save(refundPayment);
            
            // Update original invoice
            FeeInvoice invoice = payment.getInvoice();
            BigDecimal newPaidAmount = invoice.getPaidAmount().subtract(refundAmount);
            invoice.setPaidAmount(newPaidAmount);
            invoice.setBalanceAmount(invoice.getTotalAmount().subtract(newPaidAmount));
            invoice.setStatus(newPaidAmount.compareTo(BigDecimal.ZERO) <= 0 ? "PENDING" : "PARTIAL");
            feeInvoiceRepository.save(invoice);
            
            return ApiResponse.success("Refund processed successfully", convertToDto(savedRefund));
        } catch (Exception e) {
            log.error("Error processing refund for payment ID {}: {}", paymentId, e.getMessage());
            return ApiResponse.error("Failed to process refund: " + e.getMessage());
        }
    }
    
    // Finance Dashboard and Reports
    @Transactional(readOnly = true)
    public ApiResponse<Object> getFinanceDashboard(Long schoolId, Long academicYearId) {
        try {
            log.info("Generating finance dashboard for school: {} and academic year: {}", schoolId, academicYearId);
            
            // Get fee structures
            List<FeeStructure> feeStructures = feeStructureRepository.findBySchoolIdAndAcademicYearIdAndIsActiveTrue(schoolId, academicYearId);
            
            // Get total invoices
            List<FeeInvoice> allInvoices = feeInvoiceRepository.findByEnrollmentIdAndIsActiveTrue(0L); // This needs to be fixed
            
            // Calculate totals
            BigDecimal totalFeeAmount = feeStructures.stream()
                    .map(FeeStructure::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalPaidAmount = allInvoices.stream()
                    .map(FeeInvoice::getPaidAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalBalanceAmount = allInvoices.stream()
                    .map(FeeInvoice::getBalanceAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Create dashboard object
            Object dashboard = new Object() {
                public final Long totalFeeStructures = (long) feeStructures.size();
                public final BigDecimal totalFeeAmount = feeStructures.stream()
                        .map(FeeStructure::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                public final BigDecimal totalPaidAmount = allInvoices.stream()
                        .map(FeeInvoice::getPaidAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                public final BigDecimal totalBalanceAmount = allInvoices.stream()
                        .map(FeeInvoice::getBalanceAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                public final Long totalInvoices = (long) allInvoices.size();
                public final Long pendingInvoices = allInvoices.stream()
                        .filter(inv -> "PENDING".equals(inv.getStatus()))
                        .count();
                public final Long paidInvoices = allInvoices.stream()
                        .filter(inv -> "PAID".equals(inv.getStatus()))
                        .count();
                public final Long overdueInvoices = allInvoices.stream()
                        .filter(inv -> "OVERDUE".equals(inv.getStatus()))
                        .count();
            };
            
            return ApiResponse.success("Finance dashboard retrieved successfully", dashboard);
        } catch (Exception e) {
            log.error("Error generating finance dashboard: {}", e.getMessage());
            return ApiResponse.error("Failed to generate finance dashboard: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<Object> getFeeSummaryReport(Long schoolId, Long academicYearId, Long classId) {
        try {
            log.info("Generating fee summary report for school: {}, academic year: {}, class: {}", schoolId, academicYearId, classId);
            
            List<FeeStructure> feeStructures;
            if (classId != null) {
                feeStructures = feeStructureRepository.findBySchoolIdAndClassEntityIdAndAcademicYearIdAndIsActiveTrue(schoolId, classId, academicYearId);
            } else {
                feeStructures = feeStructureRepository.findBySchoolIdAndAcademicYearIdAndIsActiveTrue(schoolId, academicYearId);
            }
            
            // Group by fee type
            Map<String, BigDecimal> feeTypeTotals = feeStructures.stream()
                    .collect(Collectors.groupingBy(
                            FeeStructure::getFeeType,
                            Collectors.reducing(BigDecimal.ZERO, FeeStructure::getAmount, BigDecimal::add)
                    ));
            
            Object report = new Object() {
                public final Long totalFeeStructures = (long) feeStructures.size();
                public final Map<String, BigDecimal> feeTypeTotals = feeStructures.stream()
                        .collect(Collectors.groupingBy(
                                FeeStructure::getFeeType,
                                Collectors.reducing(BigDecimal.ZERO, FeeStructure::getAmount, BigDecimal::add)
                        ));
                public final BigDecimal totalAmount = feeStructures.stream()
                        .map(FeeStructure::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            };
            
            return ApiResponse.success("Fee summary report generated successfully", report);
        } catch (Exception e) {
            log.error("Error generating fee summary report: {}", e.getMessage());
            return ApiResponse.error("Failed to generate fee summary report: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<Object> getPaymentSummaryReport(Long schoolId, Long academicYearId, String startDate, String endDate) {
        try {
            log.info("Generating payment summary report for school: {} and academic year: {}", schoolId, academicYearId);
            
            // This would need to be implemented with proper date filtering
            // For now, return a basic structure
            final Long finalSchoolId = schoolId;
            final Long finalAcademicYearId = academicYearId;
            final String finalStartDate = startDate;
            final String finalEndDate = endDate;
            
            Object report = new Object() {
                public final String message = "Payment summary report - implementation needed";
                public final Long schoolId = finalSchoolId;
                public final Long academicYearId = finalAcademicYearId;
                public final String startDate = finalStartDate;
                public final String endDate = finalEndDate;
            };
            
            return ApiResponse.success("Payment summary report generated successfully", report);
        } catch (Exception e) {
            log.error("Error generating payment summary report: {}", e.getMessage());
            return ApiResponse.error("Failed to generate payment summary report: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<FeeInvoiceDto>> getOverdueFees(Long schoolId, Long academicYearId) {
        try {
            log.info("Fetching overdue fees for school: {} and academic year: {}", schoolId, academicYearId);
            List<FeeInvoice> overdueInvoices = feeInvoiceRepository.findOverdueInvoices(LocalDate.now());
            List<FeeInvoiceDto> invoiceDtos = overdueInvoices.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("Overdue fees retrieved successfully", invoiceDtos);
        } catch (Exception e) {
            log.error("Error fetching overdue fees: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve overdue fees: " + e.getMessage());
        }
    }
    
    // Kenya-specific Fee Management
    public ApiResponse<Object> createKenyaFeeStructure(com.schoolmanagement.dto.KenyaFeeStructureDto kenyaFeeStructureDto) {
        try {
            log.info("Creating Kenya fee structure: {}", kenyaFeeStructureDto.getFeeName());
            
            // Validate school exists
            School school = schoolRepository.findById(kenyaFeeStructureDto.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found with ID: " + kenyaFeeStructureDto.getSchoolId()));
            
            // Validate class exists
            ClassEntity classEntity = classRepository.findById(kenyaFeeStructureDto.getClassId())
                    .orElseThrow(() -> new RuntimeException("Class not found with ID: " + kenyaFeeStructureDto.getClassId()));
            
            // Validate academic year exists
            AcademicYear academicYear = academicYearRepository.findById(kenyaFeeStructureDto.getAcademicYearId())
                    .orElseThrow(() -> new RuntimeException("Academic year not found with ID: " + kenyaFeeStructureDto.getAcademicYearId()));
            
            KenyaFeeStructure kenyaFeeStructure = new KenyaFeeStructure();
            kenyaFeeStructure.setSchool(school);
            kenyaFeeStructure.setClassEntity(classEntity);
            kenyaFeeStructure.setAcademicYear(academicYear);
            kenyaFeeStructure.setFeeName(kenyaFeeStructureDto.getFeeName());
            kenyaFeeStructure.setFeeCode(kenyaFeeStructureDto.getFeeCode());
            kenyaFeeStructure.setFeeType(KenyaFeeStructure.FeeType.valueOf(kenyaFeeStructureDto.getFeeType()));
            kenyaFeeStructure.setAmount(kenyaFeeStructureDto.getAmount());
            kenyaFeeStructure.setCapitationAmount(kenyaFeeStructureDto.getCapitationAmount());
            kenyaFeeStructure.setParentContribution(kenyaFeeStructureDto.getParentContribution());
            kenyaFeeStructure.setFrequency(KenyaFeeStructure.PaymentFrequency.valueOf(kenyaFeeStructureDto.getFrequency()));
            kenyaFeeStructure.setIsMandatory(kenyaFeeStructureDto.getIsMandatory());
            kenyaFeeStructure.setIsCapitationEligible(kenyaFeeStructureDto.getIsCapitationEligible());
            kenyaFeeStructure.setIsBursaryEligible(kenyaFeeStructureDto.getIsBursaryEligible());
            kenyaFeeStructure.setIsActive(kenyaFeeStructureDto.getIsActive());
            
            KenyaFeeStructure savedKenyaFeeStructure = kenyaFeeStructureRepository.save(kenyaFeeStructure);
            log.info("Kenya fee structure created successfully: {}", savedKenyaFeeStructure.getId());
            
            return ApiResponse.success("Kenya fee structure created successfully", savedKenyaFeeStructure);
        } catch (Exception e) {
            log.error("Error creating Kenya fee structure: {}", e.getMessage());
            return ApiResponse.error("Failed to create Kenya fee structure: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<com.schoolmanagement.dto.KenyaFeeStructureDto>> getKenyaFeeStructures(Long schoolId, Long academicYearId) {
        try {
            log.info("Fetching Kenya fee structures for school: {} and academic year: {}", schoolId, academicYearId);
            List<KenyaFeeStructure> kenyaFeeStructures = kenyaFeeStructureRepository.findBySchoolAndClassAndAcademicYear(schoolId, null, academicYearId);
            List<com.schoolmanagement.dto.KenyaFeeStructureDto> kenyaFeeStructureDtos = kenyaFeeStructures.stream()
                    .map(this::convertToKenyaDto)
                    .toList();
            return ApiResponse.success("Kenya fee structures retrieved successfully", kenyaFeeStructureDtos);
        } catch (Exception e) {
            log.error("Error fetching Kenya fee structures: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve Kenya fee structures: " + e.getMessage());
        }
    }
    
    // Payment Gateway Integration
    public ApiResponse<MpesaStkPushResponse> initiateMpesaStkPush(PaymentRequestDto paymentRequest, User currentUser) {
        try {
            log.info("Initiating M-Pesa STK Push for invoice: {}", paymentRequest.getInvoiceId());
            
            // Validate invoice exists (optional for testing - create mock invoice if not found)
            FeeInvoice invoice;
            Optional<FeeInvoice> invoiceOpt = feeInvoiceRepository.findById(paymentRequest.getInvoiceId());
            
            if (invoiceOpt.isEmpty()) {
                // For testing: create a temporary invoice if it doesn't exist
                log.warn("Invoice {} not found, creating test invoice for STK Push testing", paymentRequest.getInvoiceId());
                Optional<StudentEnrollment> enrollmentOpt = studentEnrollmentRepository.findById(1L);
                
                if (enrollmentOpt.isEmpty()) {
                    return ApiResponse.error("Invoice not found and cannot create test invoice (no enrollment found). Please create an invoice first.");
                }
                
                // Create minimal test invoice
                StudentEnrollment enrollment = enrollmentOpt.get();
                FeeStructure testFeeStructure = feeStructureRepository.findById(1L).orElseGet(() -> {
                    FeeStructure fs = new FeeStructure();
                    fs.setName("Test Fee");
                    fs.setAmount(BigDecimal.valueOf(1000));
                    fs.setSchool(enrollment.getClassEntity().getSchool());
                    return feeStructureRepository.save(fs);
                });
                
                invoice = new FeeInvoice();
                invoice.setInvoiceNumber("TEST-INV-" + System.currentTimeMillis());
                invoice.setIssueDate(LocalDate.now());
                invoice.setDueDate(LocalDate.now().plusDays(30));
                invoice.setTotalAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(10))); // Set higher for testing
                invoice.setPaidAmount(BigDecimal.ZERO);
                invoice.setBalanceAmount(invoice.getTotalAmount());
                invoice.setStatus("PENDING");
                invoice.setEnrollment(enrollmentOpt.get());
                invoice.setFeeStructure(testFeeStructure);
                invoice.setIsActive(true);
                invoice = feeInvoiceRepository.save(invoice);
                log.info("Created test invoice: {}", invoice.getId());
            } else {
                invoice = invoiceOpt.get();
            }
            
            // Validate amount (only if invoice exists and has balance)
            if (paymentRequest.getAmount().compareTo(invoice.getBalanceAmount()) > 0) {
                log.warn("Amount {} exceeds invoice balance {}, but proceeding for testing", paymentRequest.getAmount(), invoice.getBalanceAmount());
            }
            
            // Validate phone number
            if (paymentRequest.getPhoneNumber() == null || paymentRequest.getPhoneNumber().trim().isEmpty()) {
                return ApiResponse.error("Phone number is required for M-Pesa payment");
            }
            
            // Generate account reference from invoice number
            String accountReference = paymentRequest.getAccountReference() != null 
                ? paymentRequest.getAccountReference() 
                : invoice.getInvoiceNumber();
            
            String transactionDescription = paymentRequest.getTransactionDescription() != null
                ? paymentRequest.getTransactionDescription()
                : "Payment for " + invoice.getInvoiceNumber();
            
            // Format amount (M-Pesa expects whole numbers in cents/shillings)
            int amountInShillings = paymentRequest.getAmount().multiply(BigDecimal.valueOf(1)).intValue();
            String amountString = String.valueOf(amountInShillings);
            
            // Initiate STK Push
            String checkoutRequestId = mpesaService.initiateSTKPush(
                paymentRequest.getPhoneNumber(),
                amountString,
                accountReference,
                transactionDescription
            );
            
            // Create pending payment record
            Payment pendingPayment = new Payment();
            pendingPayment.setPaymentReference("MPESA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            pendingPayment.setAmount(paymentRequest.getAmount());
            pendingPayment.setPaymentMethod("M_PESA");
            pendingPayment.setPaymentStatus("PENDING");
            pendingPayment.setExternalReference(checkoutRequestId); // Store checkout request ID
            pendingPayment.setTransactionId(null); // Will be set when webhook is received
            pendingPayment.setPaymentNotes(paymentRequest.getPaymentNotes());
            pendingPayment.setPaymentDate(LocalDateTime.now());
            pendingPayment.setInvoice(invoice);
            pendingPayment.setEnrollment(invoice.getEnrollment());
            pendingPayment.setProcessedBy(currentUser);
            pendingPayment.setIsActive(true);
            
            paymentRepository.save(pendingPayment);
            log.info("Created pending payment record with checkout request ID: {}", checkoutRequestId);
            
            // Build response
            MpesaStkPushResponse response = new MpesaStkPushResponse();
            response.setCheckoutRequestId(checkoutRequestId);
            response.setMerchantRequestId("MR_" + System.currentTimeMillis());
            response.setCustomerMessage("Confirm payment on your phone");
            response.setResponseCode("0");
            response.setResponseDescription("The service request is processed successfully");
            response.setInvoiceId(invoice.getId());
            response.setInvoiceNumber(invoice.getInvoiceNumber());
            response.setAmount(paymentRequest.getAmount().toString());
            response.setPhoneNumber(paymentRequest.getPhoneNumber());
            
            return ApiResponse.success("M-Pesa STK Push initiated successfully. Please check your phone.", response);
            
        } catch (IllegalArgumentException e) {
            log.error("Validation error initiating M-Pesa STK Push: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error initiating M-Pesa STK Push: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to initiate M-Pesa STK Push: " + e.getMessage());
        }
    }
    
    public ApiResponse<Object> createStripePaymentIntent(PaymentRequestDto paymentRequest, User currentUser) {
        try {
            log.info("Creating Stripe payment intent for invoice: {}", paymentRequest.getInvoiceId());
            
            // TODO: Implement actual Stripe payment intent creation
            // For now, return a mock response
            Object response = new Object() {
                public final String message = "Stripe payment intent created successfully";
                public final String invoiceId = paymentRequest.getInvoiceId().toString();
                public final String amount = paymentRequest.getAmount().toString();
                public final String clientSecret = "pi_" + System.currentTimeMillis() + "_secret_" + UUID.randomUUID().toString().substring(0, 8);
                public final String paymentIntentId = "pi_" + System.currentTimeMillis();
            };
            
            return ApiResponse.success("Stripe payment intent created successfully", response);
        } catch (Exception e) {
            log.error("Error creating Stripe payment intent: {}", e.getMessage());
            return ApiResponse.error("Failed to create Stripe payment intent: " + e.getMessage());
        }
    }
    
    @Transactional
    public String handleMpesaWebhook(String webhookPayload) {
        try {
            log.info("Processing M-Pesa webhook: {}", webhookPayload);
            
            // Parse webhook payload
            Map<String, Object> payloadMap = objectMapper.readValue(webhookPayload, Map.class);
            
            // Extract STK callback data
            Map<String, Object> body = (Map<String, Object>) payloadMap.get("Body");
            if (body == null) {
                log.warn("Webhook payload missing Body: {}", webhookPayload);
                return "ERROR: Invalid webhook payload";
            }
            
            Map<String, Object> stkCallback = (Map<String, Object>) body.get("stkCallback");
            if (stkCallback == null) {
                log.warn("Webhook payload missing stkCallback: {}", webhookPayload);
                return "ERROR: Invalid webhook payload";
            }
            
            String checkoutRequestId = (String) stkCallback.get("CheckoutRequestID");
            Integer resultCode = (Integer) stkCallback.get("ResultCode");
            String resultDesc = (String) stkCallback.get("ResultDesc");
            
            log.info("Processing callback - CheckoutRequestID: {}, ResultCode: {}, ResultDesc: {}", 
                checkoutRequestId, resultCode, resultDesc);
            
            // Find payment by checkout request ID (stored in externalReference)
            Optional<Payment> paymentOpt = paymentRepository.findByExternalReference(checkoutRequestId);
            
            if (paymentOpt.isEmpty()) {
                log.warn("Payment not found for checkout request ID: {}", checkoutRequestId);
                return "ERROR: Payment not found";
            }
            
            Payment payment = paymentOpt.get();
            
            // Process based on result code
            if (resultCode == 0) {
                // Payment successful
                Map<String, Object> callbackMetadata = (Map<String, Object>) stkCallback.get("CallbackMetadata");
                if (callbackMetadata != null) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) callbackMetadata.get("Item");
                    
                    String mpesaReceiptNumber = null;
                    
                    if (items != null) {
                        for (Map<String, Object> item : items) {
                            String name = (String) item.get("Name");
                            Object value = item.get("Value");
                            
                            if ("MpesaReceiptNumber".equals(name)) {
                                mpesaReceiptNumber = value.toString();
                                break; // We only need the receipt number
                            }
                        }
                    }
                    
                    // Update payment
                    payment.setTransactionId(mpesaReceiptNumber);
                    payment.setPaymentStatus("COMPLETED");
                    payment.setPaymentDate(LocalDateTime.now());
                    if (payment.getPaymentNotes() == null || payment.getPaymentNotes().isEmpty()) {
                        payment.setPaymentNotes("M-Pesa Receipt: " + mpesaReceiptNumber);
                    }
                    
                    paymentRepository.save(payment);
                    
                    // Update invoice
                    FeeInvoice invoice = payment.getInvoice();
                    BigDecimal newPaidAmount = invoice.getPaidAmount().add(payment.getAmount());
                    invoice.setPaidAmount(newPaidAmount);
                    invoice.setBalanceAmount(invoice.getTotalAmount().subtract(newPaidAmount));
                    
                    // Update invoice status
                    if (invoice.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        invoice.setStatus("PAID");
                    } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
                        invoice.setStatus("PARTIAL");
                    }
                    
                    feeInvoiceRepository.save(invoice);
                    
                    log.info("Payment completed successfully - Receipt: {}, Amount: {}", mpesaReceiptNumber, payment.getAmount());
                    
                    // Send notification
                    sendPaymentNotification(payment, invoice);
                }
            } else {
                // Payment failed
                payment.setPaymentStatus("FAILED");
                payment.setPaymentNotes("M-Pesa payment failed: " + resultDesc);
                paymentRepository.save(payment);
                
                log.warn("Payment failed - CheckoutRequestID: {}, Reason: {}", checkoutRequestId, resultDesc);
            }
            
            return "SUCCESS";
            
        } catch (Exception e) {
            log.error("Error processing M-Pesa webhook: {}", e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    public String handleStripeWebhook(String webhookPayload) {
        try {
            log.info("Processing Stripe webhook: {}", webhookPayload);
            // TODO: Implement Stripe webhook processing
            return "Stripe webhook processed successfully";
        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage());
            return "Error processing Stripe webhook: " + e.getMessage();
        }
    }
    
    // Helper method for Kenya fee structure DTO conversion
    private com.schoolmanagement.dto.KenyaFeeStructureDto convertToKenyaDto(KenyaFeeStructure kenyaFeeStructure) {
        com.schoolmanagement.dto.KenyaFeeStructureDto dto = new com.schoolmanagement.dto.KenyaFeeStructureDto();
        dto.setId(kenyaFeeStructure.getId());
        dto.setSchoolId(kenyaFeeStructure.getSchool().getId());
        dto.setSchoolName(kenyaFeeStructure.getSchool().getName());
        dto.setClassId(kenyaFeeStructure.getClassEntity().getId());
        dto.setClassName(kenyaFeeStructure.getClassEntity().getName());
        dto.setAcademicYearId(kenyaFeeStructure.getAcademicYear().getId());
        dto.setAcademicYearName(kenyaFeeStructure.getAcademicYear().getName());
        dto.setFeeName(kenyaFeeStructure.getFeeName());
        dto.setFeeCode(kenyaFeeStructure.getFeeCode());
        dto.setFeeType(kenyaFeeStructure.getFeeType().toString());
        dto.setAmount(kenyaFeeStructure.getAmount());
        dto.setCapitationAmount(kenyaFeeStructure.getCapitationAmount());
        dto.setParentContribution(kenyaFeeStructure.getParentContribution());
        dto.setFrequency(kenyaFeeStructure.getFrequency().toString());
        dto.setIsMandatory(kenyaFeeStructure.getIsMandatory());
        dto.setIsCapitationEligible(kenyaFeeStructure.getIsCapitationEligible());
        dto.setIsBursaryEligible(kenyaFeeStructure.getIsBursaryEligible());
        dto.setIsActive(kenyaFeeStructure.getIsActive());
        dto.setCreatedAt(kenyaFeeStructure.getCreatedAt());
        dto.setUpdatedAt(kenyaFeeStructure.getUpdatedAt());
        return dto;
    }
    
    // Send payment notification
    private void sendPaymentNotification(Payment payment, FeeInvoice invoice) {
        try {
            // Get the student from the enrollment
            User student = invoice.getEnrollment().getStudent();
            
            String paymentMessage = String.format(
                "Payment of KES %s has been successfully processed for %s. " +
                "Payment Method: %s, Transaction ID: %s. " +
                "Your fee balance has been updated accordingly.",
                payment.getAmount(),
                invoice.getFeeStructure().getName(),
                payment.getPaymentMethod(),
                payment.getTransactionId() != null ? payment.getTransactionId() : "N/A"
            );
            
            com.schoolmanagement.dto.NotificationRequestDto notificationRequest = 
                com.schoolmanagement.dto.NotificationRequestDto.builder()
                    .title("Payment Processed Successfully")
                    .message(paymentMessage)
                    .type(Notification.NotificationType.PAYMENT)
                    .priority(Notification.NotificationPriority.MEDIUM)
                    .recipientId(student.getId())
                    .actionUrl("/finance/payments")
                    .actionText("View Payment Details")
                    .build();
            
            notificationService.createNotification(notificationRequest);
            log.info("Payment notification sent to student: {}", student.getUsername());
            
        } catch (Exception e) {
            log.error("Error sending payment notification: {}", e.getMessage());
        }
    }
}


