package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.DashboardDto;
import com.schoolmanagement.dto.ReportDto;
import com.schoolmanagement.dto.ReportRequestDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportingService {
    
    private final ReportRepository reportRepository;
    private final DashboardWidgetRepository dashboardWidgetRepository;
    private final SchoolRepository schoolRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final FeeInvoiceRepository feeInvoiceRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExamRepository examRepository;
    
    // Dashboard Data
    @Transactional(readOnly = true)
    public ApiResponse<DashboardDto> getDashboardData(Long schoolId) {
        try {
            log.info("Fetching dashboard data for school: {}", schoolId);
            
            // Get school info
            Optional<School> school = schoolRepository.findById(schoolId);
            if (school.isEmpty()) {
                return ApiResponse.error("School not found");
            }
            
            DashboardDto dashboard = new DashboardDto();
            dashboard.setSchoolId(schoolId);
            dashboard.setSchoolName(school.get().getName());
            
            // Get key metrics
            dashboard.setTotalStudents(getTotalStudents(schoolId));
            dashboard.setTotalTeachers(getTotalTeachers(schoolId));
            dashboard.setTotalClasses(getTotalClasses(schoolId));
            dashboard.setTotalSubjects(getTotalSubjects(schoolId));
            
            // Get financial metrics
            dashboard.setTotalFeeCollection(getTotalFeeCollection(schoolId));
            dashboard.setPendingFees(getPendingFees(schoolId));
            dashboard.setOverdueFees(getOverdueFees(schoolId));
            
            // Get academic metrics
            dashboard.setAverageAttendance(getAverageAttendance(schoolId));
            dashboard.setTotalExams(getTotalExams(schoolId));
            dashboard.setCompletedExams(getCompletedExams(schoolId));
            
            // Get chart data
            dashboard.setAttendanceChart(getAttendanceChartData(schoolId));
            dashboard.setFeeCollectionChart(getFeeCollectionChartData(schoolId));
            dashboard.setPerformanceChart(getPerformanceChartData(schoolId));
            
            // Get recent activities
            dashboard.setRecentActivities(getRecentActivities(schoolId));
            
            // Get widgets
            dashboard.setWidgets(getDashboardWidgets(schoolId));
            
            return ApiResponse.success("Dashboard data retrieved successfully", dashboard);
            
        } catch (Exception e) {
            log.error("Error fetching dashboard data: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve dashboard data: " + e.getMessage());
        }
    }
    
    // Report Generation
    public ApiResponse<ReportDto> generateReport(ReportRequestDto reportRequest, User generatedBy) {
        try {
            log.info("Generating report: {} for user: {}", reportRequest.getName(), generatedBy.getUsername());
            
            // Get school from user's enrollment or use default school
            School school = schoolRepository.findById(1L).orElse(null);
            if (school == null) {
                return ApiResponse.error("School not found");
            }
            
            Report report = new Report();
            report.setName(reportRequest.getName());
            report.setDescription(reportRequest.getDescription());
            report.setReportType(reportRequest.getReportType());
            report.setStatus("PENDING");
            report.setFileFormat(reportRequest.getFileFormat());
            report.setGeneratedAt(LocalDateTime.now());
            report.setSchool(school);
            report.setGeneratedBy(generatedBy);
            
            // Set parameters as JSON string
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("startDate", reportRequest.getStartDate());
            parameters.put("endDate", reportRequest.getEndDate());
            parameters.put("classId", reportRequest.getClassId());
            parameters.put("subjectId", reportRequest.getSubjectId());
            parameters.put("studentId", reportRequest.getStudentId());
            parameters.put("teacherId", reportRequest.getTeacherId());
            parameters.put("additionalParameters", reportRequest.getAdditionalParameters());
            
            report.setParameters(parameters.toString());
            
            Report savedReport = reportRepository.save(report);
            
            // Simulate report generation (in real implementation, this would be async)
            generateReportContent(savedReport);
            
            return ApiResponse.success("Report generated successfully", convertToDto(savedReport));
            
        } catch (Exception e) {
            log.error("Error generating report: {}", e.getMessage());
            return ApiResponse.error("Failed to generate report: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<ReportDto>> getReports(Long schoolId, String reportType) {
        try {
            log.info("Fetching reports for school: {} and type: {}", schoolId, reportType);
            
            List<Report> reports;
            if (reportType != null && !reportType.isEmpty()) {
                reports = reportRepository.findBySchoolIdAndReportTypeAndIsActiveTrue(schoolId, reportType);
            } else {
                reports = reportRepository.findBySchoolIdAndIsActiveTrue(schoolId);
            }
            
            List<ReportDto> reportDtos = reports.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Reports retrieved successfully", reportDtos);
            
        } catch (Exception e) {
            log.error("Error fetching reports: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve reports: " + e.getMessage());
        }
    }
    
    // Helper methods for dashboard metrics
    private Long getTotalStudents(Long schoolId) {
        return studentEnrollmentRepository.count();
    }
    
    private Long getTotalTeachers(Long schoolId) {
        return 10L; // Placeholder
    }
    
    private Long getTotalClasses(Long schoolId) {
        return 5L; // Placeholder
    }
    
    private Long getTotalSubjects(Long schoolId) {
        return 15L; // Placeholder
    }
    
    private Double getTotalFeeCollection(Long schoolId) {
        return 150000.0; // Placeholder
    }
    
    private Double getPendingFees(Long schoolId) {
        return 25000.0; // Placeholder
    }
    
    private Double getOverdueFees(Long schoolId) {
        return 5000.0; // Placeholder
    }
    
    private Double getAverageAttendance(Long schoolId) {
        return 85.5; // Placeholder
    }
    
    private Long getTotalExams(Long schoolId) {
        return 20L; // Placeholder
    }
    
    private Long getCompletedExams(Long schoolId) {
        return 15L; // Placeholder
    }
    
    // Chart data methods
    private Map<String, Object> getAttendanceChartData(Long schoolId) {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("type", "line");
        chartData.put("title", "Attendance Trend");
        chartData.put("data", Arrays.asList(
            Map.of("date", "2025-01-01", "attendance", 85),
            Map.of("date", "2025-01-02", "attendance", 87),
            Map.of("date", "2025-01-03", "attendance", 83)
        ));
        return chartData;
    }
    
    private Map<String, Object> getFeeCollectionChartData(Long schoolId) {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("type", "bar");
        chartData.put("title", "Fee Collection");
        chartData.put("data", Arrays.asList(
            Map.of("month", "January", "amount", 50000),
            Map.of("month", "February", "amount", 45000),
            Map.of("month", "March", "amount", 55000)
        ));
        return chartData;
    }
    
    private Map<String, Object> getPerformanceChartData(Long schoolId) {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("type", "pie");
        chartData.put("title", "Student Performance");
        chartData.put("data", Arrays.asList(
            Map.of("grade", "A", "count", 25),
            Map.of("grade", "B", "count", 35),
            Map.of("grade", "C", "count", 20)
        ));
        return chartData;
    }
    
    private List<Map<String, Object>> getRecentActivities(Long schoolId) {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // Add sample activities
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("type", "payment");
        activity1.put("description", "Payment received: KSh 50,000");
        activity1.put("timestamp", LocalDateTime.now().minusHours(2));
        activities.add(activity1);
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("type", "enrollment");
        activity2.put("description", "New student enrolled");
        activity2.put("timestamp", LocalDateTime.now().minusHours(5));
        activities.add(activity2);
        
        return activities;
    }
    
    private List<Map<String, Object>> getDashboardWidgets(Long schoolId) {
        List<DashboardWidget> widgets = dashboardWidgetRepository.findActiveWidgetsBySchoolOrdered(schoolId);
        return widgets.stream()
                .map(this::convertWidgetToMap)
                .toList();
    }
    
    private Map<String, Object> convertWidgetToMap(DashboardWidget widget) {
        Map<String, Object> widgetMap = new HashMap<>();
        widgetMap.put("id", widget.getId());
        widgetMap.put("name", widget.getName());
        widgetMap.put("type", widget.getWidgetType());
        widgetMap.put("dataSource", widget.getDataSource());
        widgetMap.put("positionX", widget.getPositionX());
        widgetMap.put("positionY", widget.getPositionY());
        widgetMap.put("width", widget.getWidth());
        widgetMap.put("height", widget.getHeight());
        widgetMap.put("configuration", widget.getConfiguration());
        return widgetMap;
    }
    
    private void generateReportContent(Report report) {
        // Simulate report generation
        report.setStatus("COMPLETED");
        report.setFilePath("/reports/" + report.getId() + "." + report.getFileFormat().toLowerCase());
        reportRepository.save(report);
    }
    
    private ReportDto convertToDto(Report report) {
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setName(report.getName());
        dto.setDescription(report.getDescription());
        dto.setReportType(report.getReportType());
        dto.setStatus(report.getStatus());
        dto.setParameters(report.getParameters());
        dto.setFilePath(report.getFilePath());
        dto.setFileFormat(report.getFileFormat());
        dto.setGeneratedAt(report.getGeneratedAt());
        dto.setIsActive(report.getIsActive());
        dto.setSchoolId(report.getSchool().getId());
        dto.setSchoolName(report.getSchool().getName());
        dto.setGeneratedById(report.getGeneratedBy().getId());
        dto.setGeneratedByName(report.getGeneratedBy().getFirstName() + " " + report.getGeneratedBy().getLastName());
        return dto;
    }
}
