package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "naming_series")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NamingSeries {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50, unique = true)
    private String name; // e.g., "EMP", "STU", "TCH", "SUP"
    
    @Column(nullable = false, length = 100)
    private String description; // e.g., "Employee ID Series", "Student Admission Number Series"
    
    @Column(nullable = false, length = 20)
    private String prefix; // e.g., "EMP", "STU", "TCH", "SUP"
    
    @Column(nullable = false, length = 20)
    private String suffix; // e.g., "001", "2024", "KE"
    
    @Column(nullable = false)
    private Integer startNumber = 1; // Starting number for the series
    
    @Column(nullable = false)
    private Integer currentNumber = 1; // Current number in the series
    
    @Column(nullable = false)
    private Integer padding = 3; // Number of digits to pad (e.g., 3 for 001, 4 for 0001)
    
    @Column(nullable = false, length = 10)
    private String separator = "-"; // Separator between prefix and number
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isDefault = false; // Default series for this type
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeriesType seriesType; // EMPLOYEE, STUDENT, TEACHER, SUPPORT_STAFF
    
    @Column(length = 500)
    private String notes; // Additional notes about the series
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum SeriesType {
        EMPLOYEE,
        STUDENT,
        TEACHER,
        SUPPORT_STAFF,
        PARENT,
        ADMIN
    }
    
    // Method to generate the next ID in the series
    public String generateNextId() {
        String paddedNumber = String.format("%0" + padding + "d", currentNumber);
        String generatedId = prefix + separator + paddedNumber;
        
        // Update current number for next generation
        currentNumber++;
        
        return generatedId;
    }
    
    // Method to get the next ID without updating the counter
    public String getNextId() {
        String paddedNumber = String.format("%0" + padding + "d", currentNumber);
        return prefix + separator + paddedNumber;
    }
    
    // Method to reset the series
    public void resetSeries() {
        currentNumber = startNumber;
    }
}
