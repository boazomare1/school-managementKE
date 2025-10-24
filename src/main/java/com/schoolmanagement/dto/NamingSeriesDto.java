package com.schoolmanagement.dto;

import com.schoolmanagement.entity.NamingSeries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NamingSeriesDto {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Prefix is required")
    private String prefix;
    
    @NotBlank(message = "Suffix is required")
    private String suffix;
    
    @Positive(message = "Start number must be positive")
    private Integer startNumber = 1;
    
    @Positive(message = "Current number must be positive")
    private Integer currentNumber = 1;
    
    @Positive(message = "Padding must be positive")
    private Integer padding = 3;
    
    private String separator = "-";
    
    private Boolean isActive = true;
    
    private Boolean isDefault = false;
    
    @NotNull(message = "Series type is required")
    private NamingSeries.SeriesType seriesType;
    
    private String notes;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    private String nextId;
    private String exampleId;
}
