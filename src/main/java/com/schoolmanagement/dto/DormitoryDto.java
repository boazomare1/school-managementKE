package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DormitoryDto {
    
    private Long id;
    
    @NotBlank(message = "Dormitory name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;
    
    private Integer totalRooms;
    private Integer currentOccupancy;
    private Integer availableRooms;
    private Integer occupiedRooms;
    private BigDecimal monthlyFee;
    private Boolean isActive;
    private String status;
    private String type;
    private String rules;
    private String notes;
    
    private Long dormMasterId;
    private String dormMasterName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
