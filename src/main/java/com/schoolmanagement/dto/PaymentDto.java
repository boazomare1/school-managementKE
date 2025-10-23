package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    
    private Long id;
    
    @NotBlank(message = "Payment reference is required")
    @Size(max = 50, message = "Payment reference must not exceed 50 characters")
    private String paymentReference;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    @Size(max = 20, message = "Payment method must not exceed 20 characters")
    private String paymentMethod;
    
    @NotBlank(message = "Payment status is required")
    @Size(max = 20, message = "Payment status must not exceed 20 characters")
    private String paymentStatus;
    
    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    private String transactionId;
    
    @Size(max = 100, message = "External reference must not exceed 100 characters")
    private String externalReference;
    
    @Size(max = 500, message = "Payment notes must not exceed 500 characters")
    private String paymentNotes;
    
    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;
    
    private Boolean isActive;
    
    private Long invoiceId;
    private String invoiceNumber;
    
    private Long enrollmentId;
    private String studentName;
    private String studentEmail;
    
    private Long processedById;
    private String processedByName;
}


