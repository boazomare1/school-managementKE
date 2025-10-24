package com.schoolmanagement.dto;

import com.schoolmanagement.entity.PayrollItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollItemDto {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotNull(message = "Type is required")
    private PayrollItem.PayrollItemType type;
    
    private Boolean isActive = true;
    
    private Boolean isMandatory = false;
    
    private Boolean isPercentage = false;
    
    @PositiveOrZero(message = "Fixed amount must be positive or zero")
    private BigDecimal fixedAmount;
    
    @PositiveOrZero(message = "Percentage rate must be positive or zero")
    private BigDecimal percentageRate;
    
    @PositiveOrZero(message = "Minimum amount must be positive or zero")
    private BigDecimal minimumAmount;
    
    @PositiveOrZero(message = "Maximum amount must be positive or zero")
    private BigDecimal maximumAmount;
    
    private Boolean isTaxable = true;
    
    private String description;
    
    private String category;
    
    private String governmentCode;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
