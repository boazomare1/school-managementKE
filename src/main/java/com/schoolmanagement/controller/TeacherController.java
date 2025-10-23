package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.AssignmentDto;
import com.schoolmanagement.dto.AssignmentRequestDto;
import com.schoolmanagement.dto.AssignmentSubmissionDto;
import com.schoolmanagement.dto.AssignmentSubmissionRequestDto;
import com.schoolmanagement.dto.DocumentDto;
import com.schoolmanagement.dto.DocumentUploadRequestDto;
import com.schoolmanagement.dto.LessonPlanDto;
import com.schoolmanagement.dto.LessonPlanRequestDto;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@Slf4j
public class TeacherController {

    private final TeacherService teacherService;

    // Document Management
    @PostMapping("/documents/upload")
    public ResponseEntity<ApiResponse<DocumentDto>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "classId", required = false) Long classId,
            @RequestParam(value = "academicYearId", required = false) Long academicYearId,
            @RequestParam(value = "examId", required = false) Long examId,
            @RequestParam(value = "assignmentId", required = false) Long assignmentId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", defaultValue = "false") Boolean isPublic,
            Authentication authentication) {
        
        log.info("Uploading document: {} by teacher: {}", file.getOriginalFilename(), authentication.getName());
        
        User teacher = (User) authentication.getPrincipal();
        
        DocumentUploadRequestDto request = DocumentUploadRequestDto.builder()
                .documentType(documentType)
                .subjectId(subjectId)
                .classId(classId)
                .academicYearId(academicYearId)
                .examId(examId)
                .assignmentId(assignmentId)
                .title(title)
                .description(description)
                .isPublic(isPublic)
                .build();
        
        ApiResponse<DocumentDto> response = teacherService.uploadDocument(file, request, teacher);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    // Assignment Management
    @PostMapping("/assignments")
    public ResponseEntity<ApiResponse<AssignmentDto>> createAssignment(
            @Valid @RequestBody AssignmentRequestDto request, Authentication authentication) {
        log.info("Creating assignment: {} by teacher: {}", request.getTitle(), authentication.getName());
        User teacher = (User) authentication.getPrincipal();
        ApiResponse<AssignmentDto> response = teacherService.createAssignment(request, teacher);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PutMapping("/assignments/{assignmentId}/publish")
    public ResponseEntity<ApiResponse<AssignmentDto>> publishAssignment(
            @PathVariable Long assignmentId, Authentication authentication) {
        log.info("Publishing assignment: {} by teacher: {}", assignmentId, authentication.getName());
        User teacher = (User) authentication.getPrincipal();
        ApiResponse<AssignmentDto> response = teacherService.publishAssignment(assignmentId, teacher);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<List<AssignmentDto>>> getTeacherAssignments(Authentication authentication) {
        log.info("Fetching assignments for teacher: {}", authentication.getName());
        User teacher = (User) authentication.getPrincipal();
        ApiResponse<List<AssignmentDto>> response = teacherService.getTeacherAssignments(teacher.getId());
        return ResponseEntity.ok(response);
    }

    // Assignment Submission Management
    @PostMapping("/assignments/{assignmentId}/submit")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> submitAssignment(
            @PathVariable Long assignmentId,
            @Valid @RequestBody AssignmentSubmissionRequestDto request, Authentication authentication) {
        log.info("Submitting assignment: {} by student: {}", assignmentId, authentication.getName());
        User student = (User) authentication.getPrincipal();
        request.setAssignmentId(assignmentId);
        request.setStudentId(student.getId());
        ApiResponse<AssignmentSubmissionDto> response = teacherService.submitAssignment(request, student);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<ApiResponse<List<AssignmentSubmissionDto>>> getAssignmentSubmissions(
            @PathVariable Long assignmentId) {
        log.info("Fetching submissions for assignment: {}", assignmentId);
        ApiResponse<List<AssignmentSubmissionDto>> response = teacherService.getAssignmentSubmissions(assignmentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam BigDecimal score,
            @RequestParam String grade,
            @RequestParam(required = false) String feedback,
            Authentication authentication) {
        log.info("Grading submission: {} by teacher: {}", submissionId, authentication.getName());
        User teacher = (User) authentication.getPrincipal();
        ApiResponse<AssignmentSubmissionDto> response = teacherService.gradeSubmission(submissionId, score, grade, feedback, teacher);
        return ResponseEntity.ok(response);
    }

    // Lesson Plan Management
    @PostMapping("/lesson-plans")
    public ResponseEntity<ApiResponse<LessonPlanDto>> createLessonPlan(
            @Valid @RequestBody LessonPlanRequestDto request, Authentication authentication) {
        log.info("Creating lesson plan: {} by teacher: {}", request.getTitle(), authentication.getName());
        User teacher = (User) authentication.getPrincipal();
        ApiResponse<LessonPlanDto> response = teacherService.createLessonPlan(request, teacher);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    // Teacher Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Object>> getTeacherDashboard(Authentication authentication) {
        log.info("Fetching dashboard for teacher: {}", authentication.getName());
        User teacher = (User) authentication.getPrincipal();
        
        // Get teacher's assignments
        ApiResponse<List<AssignmentDto>> assignmentsResponse = teacherService.getTeacherAssignments(teacher.getId());
        
        // Create dashboard object
        Object dashboard = new Object() {
            public final List<AssignmentDto> assignments = assignmentsResponse.getData();
            public final int totalAssignments = assignmentsResponse.getData() != null ? assignmentsResponse.getData().size() : 0;
            public final long publishedAssignments = assignmentsResponse.getData() != null ? 
                    assignmentsResponse.getData().stream().filter(a -> "PUBLISHED".equals(a.getStatus())).count() : 0;
            public final long draftAssignments = assignmentsResponse.getData() != null ? 
                    assignmentsResponse.getData().stream().filter(a -> "DRAFT".equals(a.getStatus())).count() : 0;
        };
        
        ApiResponse<Object> response = ApiResponse.success("Teacher dashboard retrieved successfully", dashboard);
        return ResponseEntity.ok(response);
    }
}

