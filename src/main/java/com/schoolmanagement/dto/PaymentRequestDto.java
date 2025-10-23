package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    @Size(max = 20, message = "Payment method must not exceed 20 characters")
    private String paymentMethod;
    
    @Size(max = 100, message = "Phone number must not exceed 100 characters")
    private String phoneNumber; // For M-Pesa
    
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email; // For Stripe/PayPal
    
    @Size(max = 100, message = "Card token must not exceed 100 characters")
    private String cardToken; // For Stripe
    
    @Size(max = 500, message = "Payment notes must not exceed 500 characters")
    private String paymentNotes;
    
    // M-Pesa specific fields
    @Size(max = 100, message = "Account reference must not exceed 100 characters")
    private String accountReference;
    
    @Size(max = 100, message = "Transaction description must not exceed 100 characters")
    private String transactionDescription;
    
    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    private String transactionId;
    
    @Size(max = 100, message = "External reference must not exceed 100 characters")
    private String externalReference;
}
