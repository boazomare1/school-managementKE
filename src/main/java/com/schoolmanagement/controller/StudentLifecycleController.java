package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.AttendanceRequestDto;
import com.schoolmanagement.dto.ExamResultRequestDto;
import com.schoolmanagement.dto.GradeTransitionRequestDto;
import com.schoolmanagement.dto.StudentAttendanceDto;
import com.schoolmanagement.dto.StudentEnrollmentRequestDto;
import com.schoolmanagement.dto.StudentExamDto;
import com.schoolmanagement.dto.StudentFeeDto;
import com.schoolmanagement.dto.StudentGradeDto;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.service.StudentLifecycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student-lifecycle")
@RequiredArgsConstructor
@Slf4j
public class StudentLifecycleController {

    private final StudentLifecycleService studentLifecycleService;

    // Student Enrollment
    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<StudentGradeDto>> enrollStudent(@Valid @RequestBody StudentEnrollmentRequestDto request) {
        log.info("Enrolling student in grade: {}", request);
        ApiResponse<StudentGradeDto> response = studentLifecycleService.enrollStudentInGrade(request);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<ApiResponse<List<StudentGradeDto>>> getStudentEnrollments(@PathVariable Long studentId) {
        log.info("Fetching enrollments for student: {}", studentId);
        ApiResponse<List<StudentGradeDto>> response = studentLifecycleService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(response);
    }

    // Attendance Management
    @PostMapping("/attendance")
    public ResponseEntity<ApiResponse<StudentAttendanceDto>> markAttendance(
            @Valid @RequestBody AttendanceRequestDto request, Authentication authentication) {
        log.info("Marking attendance: {}", request);
        User currentUser = (User) authentication.getPrincipal();
        ApiResponse<StudentAttendanceDto> response = studentLifecycleService.markAttendance(request, currentUser);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @GetMapping("/attendance/student/{studentId}")
    public ResponseEntity<ApiResponse<List<StudentAttendanceDto>>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        log.info("Fetching attendance for student: {} from {} to {}", studentId, startDate, endDate);
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();
        
        ApiResponse<List<StudentAttendanceDto>> response = studentLifecycleService.getStudentAttendance(studentId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    // Exam Management
    @PostMapping("/exam-result")
    public ResponseEntity<ApiResponse<StudentExamDto>> recordExamResult(
            @Valid @RequestBody ExamResultRequestDto request, Authentication authentication) {
        log.info("Recording exam result: {}", request);
        User currentUser = (User) authentication.getPrincipal();
        ApiResponse<StudentExamDto> response = studentLifecycleService.recordExamResult(request, currentUser);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    // Fee Management
    @PostMapping("/fee/student/{studentId}")
    public ResponseEntity<ApiResponse<StudentFeeDto>> createStudentFee(
            @PathVariable Long studentId,
            @RequestParam Long feeStructureId,
            @RequestParam Long termId) {
        log.info("Creating fee for student: {} with fee structure: {}", studentId, feeStructureId);
        ApiResponse<StudentFeeDto> response = studentLifecycleService.createStudentFee(studentId, feeStructureId, termId);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    // Grade Transition
    @PostMapping("/transition")
    public ResponseEntity<ApiResponse<String>> transitionStudents(@Valid @RequestBody GradeTransitionRequestDto request) {
        log.info("Transitioning students: {}", request);
        ApiResponse<String> response = studentLifecycleService.transitionStudents(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    // Bulk Operations
    @PostMapping("/attendance/bulk")
    public ResponseEntity<ApiResponse<String>> markBulkAttendance(
            @RequestBody List<AttendanceRequestDto> requests, Authentication authentication) {
        log.info("Marking bulk attendance for {} students", requests.size());
        User currentUser = (User) authentication.getPrincipal();
        
        int successCount = 0;
        int errorCount = 0;
        
        for (AttendanceRequestDto request : requests) {
            ApiResponse<StudentAttendanceDto> response = studentLifecycleService.markAttendance(request, currentUser);
            if (response.isSuccess()) {
                successCount++;
            } else {
                errorCount++;
            }
        }
        
        String message = String.format("Bulk attendance completed: %d successful, %d failed", successCount, errorCount);
        ApiResponse<String> result = successCount > 0 ? 
                ApiResponse.success(message, null) : 
                ApiResponse.error("All attendance marking failed");
        
        return ResponseEntity.status(successCount > 0 ? 200 : 400).body(result);
    }

    // Student Dashboard
    @GetMapping("/dashboard/student/{studentId}")
    public ResponseEntity<ApiResponse<Object>> getStudentDashboard(@PathVariable Long studentId) {
        log.info("Fetching dashboard for student: {}", studentId);
        
        // Get current enrollment
        ApiResponse<List<StudentGradeDto>> enrollmentResponse = studentLifecycleService.getStudentEnrollments(studentId);
        
        // Get recent attendance (last 30 days)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        ApiResponse<List<StudentAttendanceDto>> attendanceResponse = studentLifecycleService.getStudentAttendance(studentId, startDate, endDate);
        
        // Create dashboard object
        Object dashboard = new Object() {
            public final List<StudentGradeDto> enrollments = enrollmentResponse.getData();
            public final List<StudentAttendanceDto> recentAttendance = attendanceResponse.getData();
            public final int totalDays = attendanceResponse.getData() != null ? attendanceResponse.getData().size() : 0;
            public final long presentDays = attendanceResponse.getData() != null ? 
                    attendanceResponse.getData().stream().filter(a -> "PRESENT".equals(a.getStatus())).count() : 0;
            public final double attendancePercentage = totalDays > 0 ? (double) presentDays / totalDays * 100 : 0;
        };
        
        ApiResponse<Object> response = ApiResponse.success("Student dashboard retrieved successfully", dashboard);
        return ResponseEntity.ok(response);
    }
}

