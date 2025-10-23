package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    
    private Long schoolId;
    private String schoolName;
    
    // Key Metrics
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalClasses;
    private Long totalSubjects;
    
    // Financial Metrics
    private Double totalFeeCollection;
    private Double pendingFees;
    private Double overdueFees;
    
    // Academic Metrics
    private Double averageAttendance;
    private Long totalExams;
    private Long completedExams;
    
    // Recent Activities
    private List<Map<String, Object>> recentActivities;
    
    // Charts Data
    private Map<String, Object> attendanceChart;
    private Map<String, Object> feeCollectionChart;
    private Map<String, Object> performanceChart;
    
    // Widgets
    private List<Map<String, Object>> widgets;
}


