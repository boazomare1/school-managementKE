package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeInvoiceDto {
    
    private Long id;
    
    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceNumber;
    
    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;
    
    @DecimalMin(value = "0.0", message = "Paid amount must be 0 or greater")
    private BigDecimal paidAmount;
    
    @DecimalMin(value = "0.0", message = "Balance amount must be 0 or greater")
    private BigDecimal balanceAmount;
    
    @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    private Boolean isActive;
    
    private Long enrollmentId;
    private String studentName;
    private String studentEmail;
    
    private Long feeStructureId;
    private String feeStructureName;
    
    private String className;
    private String academicYearName;
}


