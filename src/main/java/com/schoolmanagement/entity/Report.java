package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, length = 50)
    private String reportType; // STUDENT_PERFORMANCE, ATTENDANCE, FEE_COLLECTION, TEACHER_WORKLOAD
    
    @Column(nullable = false, length = 20)
    private String status; // PENDING, GENERATING, COMPLETED, FAILED
    
    @Column(length = 1000)
    private String parameters; // JSON string of report parameters
    
    @Column(length = 500)
    private String filePath;
    
    @Column(length = 50)
    private String fileFormat; // PDF, EXCEL, CSV
    
    @Column(nullable = false)
    private LocalDateTime generatedAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by_id", nullable = false)
    private User generatedBy;
}


