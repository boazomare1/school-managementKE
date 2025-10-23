package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.DashboardDto;
import com.schoolmanagement.dto.ReportDto;
import com.schoolmanagement.dto.ReportRequestDto;
import com.schoolmanagement.service.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportingController {
    
    private final ReportingService reportingService;
    
    // Dashboard
    @GetMapping("/dashboard/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<DashboardDto>> getDashboard(@PathVariable Long schoolId) {
        log.info("Get dashboard request for school: {}", schoolId);
        return ResponseEntity.ok(reportingService.getDashboardData(schoolId));
    }
    
    // Report Generation
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ReportDto>> generateReport(
            @Valid @RequestBody ReportRequestDto reportRequest,
            Authentication authentication) {
        log.info("Generate report request: {}", reportRequest.getName());
        
        // Get current user from authentication
        com.schoolmanagement.entity.User currentUser = (com.schoolmanagement.entity.User) authentication.getPrincipal();
        
        return ResponseEntity.ok(reportingService.generateReport(reportRequest, currentUser));
    }
    
    // Get Reports
    @GetMapping("/school/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<ReportDto>>> getReports(
            @PathVariable Long schoolId,
            @RequestParam(required = false) String reportType) {
        log.info("Get reports request for school: {} and type: {}", schoolId, reportType);
        return ResponseEntity.ok(reportingService.getReports(schoolId, reportType));
    }
    
    // Student Performance Report
    @GetMapping("/student-performance/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentPerformanceReport(@PathVariable Long studentId) {
        log.info("Get student performance report for student: {}", studentId);
        // Implementation would go here
        return ResponseEntity.ok(ApiResponse.success("Student performance report retrieved", new java.util.HashMap<>()));
    }
    
    // Attendance Report
    @GetMapping("/attendance/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAttendanceReport(@PathVariable Long classId) {
        log.info("Get attendance report for class: {}", classId);
        // Implementation would go here
        return ResponseEntity.ok(ApiResponse.success("Attendance report retrieved", new java.util.HashMap<>()));
    }
    
    // Fee Collection Report
    @GetMapping("/fee-collection/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFeeCollectionReport(@PathVariable Long schoolId) {
        log.info("Get fee collection report for school: {}", schoolId);
        // Implementation would go here
        return ResponseEntity.ok(ApiResponse.success("Fee collection report retrieved", new java.util.HashMap<>()));
    }
}
