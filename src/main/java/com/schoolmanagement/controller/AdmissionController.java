package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.AdmissionApplicationDto;
import com.schoolmanagement.entity.AdmissionApplication;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.AdmissionApplicationRepository;
import com.schoolmanagement.service.AdmissionService;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admissions")
@RequiredArgsConstructor
@Slf4j
public class AdmissionController {

    private final AdmissionService admissionService;
    private final AdmissionApplicationRepository admissionApplicationRepository;

    @PostMapping("/apply")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<AdmissionApplicationDto>> submitApplication(
            @RequestBody AdmissionApplication application,
            Authentication authentication) {
        try {
            log.info("Submitting admission application for: {} {}", application.getFirstName(), application.getLastName());
            ApiResponse<AdmissionApplication> response = admissionService.submitApplication(application);
            
            if (response.isSuccess()) {
                AdmissionApplicationDto applicationDto = convertToDto(response.getData());
                return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", applicationDto));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error submitting application: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to submit application: " + e.getMessage()));
        }
    }

    @PutMapping("/{applicationId}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMISSION_OFFICER')")
    public ResponseEntity<ApiResponse<AdmissionApplicationDto>> reviewApplication(
            @PathVariable Long applicationId,
            @RequestParam String reviewNotes,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            log.info("Reviewing application: {} by user: {}", applicationId, user.getUsername());
            ApiResponse<AdmissionApplication> response = admissionService.reviewApplication(applicationId, reviewNotes, user);
            
            if (response.isSuccess()) {
                AdmissionApplicationDto applicationDto = convertToDto(response.getData());
                return ResponseEntity.ok(ApiResponse.success("Application review completed", applicationDto));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error reviewing application: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to review application: " + e.getMessage()));
        }
    }

    @PutMapping("/{applicationId}/decision")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMISSION_OFFICER')")
    public ResponseEntity<ApiResponse<AdmissionApplicationDto>> makeDecision(
            @PathVariable Long applicationId,
            @RequestParam AdmissionApplication.ApplicationStatus decision,
            @RequestParam String decisionReason,
            Authentication authentication) {
        try {
            log.info("Making decision for application: {} - {}", applicationId, decision);
            User user = (User) authentication.getPrincipal();
            ApiResponse<AdmissionApplication> response = admissionService.makeDecision(applicationId, decision, decisionReason, user);
            
            if (response.isSuccess()) {
                AdmissionApplicationDto applicationDto = convertToDto(response.getData());
                return ResponseEntity.ok(ApiResponse.success("Application decision made", applicationDto));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error making application decision: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to make application decision: " + e.getMessage()));
        }
    }

    @PostMapping("/{applicationId}/enroll")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMISSION_OFFICER')")
    public ResponseEntity<ApiResponse<User>> enrollAcceptedStudent(
            @PathVariable Long applicationId,
            Authentication authentication) {
        try {
            log.info("Enrolling accepted student from application: {}", applicationId);
            User user = (User) authentication.getPrincipal();
            ApiResponse<User> response = admissionService.enrollAcceptedStudent(applicationId, user);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("Student enrolled successfully", response.getData()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error enrolling student: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to enroll student: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMISSION_OFFICER')")
    public ResponseEntity<ApiResponse<List<AdmissionApplicationDto>>> getApplicationsByStatus(
            @RequestParam AdmissionApplication.ApplicationStatus status) {
        try {
            log.info("Fetching applications with status: {}", status);
            ApiResponse<List<AdmissionApplication>> response = admissionService.getApplicationsByStatus(status);
            
            if (response.isSuccess()) {
                List<AdmissionApplicationDto> applicationDtos = response.getData().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", applicationDtos));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error fetching applications: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch applications: " + e.getMessage()));
        }
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMISSION_OFFICER')")
    public ResponseEntity<ApiResponse<List<AdmissionApplicationDto>>> getApplicationsByClass(@PathVariable Long classId) {
        try {
            log.info("Fetching applications for class: {}", classId);
            ApiResponse<List<AdmissionApplication>> response = admissionService.getApplicationsByClass(classId);
            
            if (response.isSuccess()) {
                List<AdmissionApplicationDto> applicationDtos = response.getData().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", applicationDtos));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Error fetching applications by class: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch applications: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMISSION_OFFICER')")
    public ResponseEntity<ApiResponse<Object>> getAdmissionStatistics(@RequestParam Long academicYearId) {
        try {
            log.info("Fetching admission statistics for academic year: {}", academicYearId);
            ApiResponse<Object> response = admissionService.getAdmissionStatistics(academicYearId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching admission statistics: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch admission statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMISSION_OFFICER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<AdmissionApplicationDto>> getApplicationById(@PathVariable Long id) {
        try {
            log.info("Fetching application with ID: {}", id);
            Optional<AdmissionApplication> applicationOptional = admissionApplicationRepository.findById(id);
            if (applicationOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            AdmissionApplicationDto applicationDto = convertToDto(applicationOptional.get());
            return ResponseEntity.ok(ApiResponse.success("Application retrieved successfully", applicationDto));
        } catch (Exception e) {
            log.error("Error fetching application by ID: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch application: " + e.getMessage()));
        }
    }

    @GetMapping("/number/{applicationNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMISSION_OFFICER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<AdmissionApplicationDto>> getApplicationByNumber(@PathVariable String applicationNumber) {
        try {
            log.info("Fetching application with number: {}", applicationNumber);
            Optional<AdmissionApplication> applicationOptional = admissionApplicationRepository.findByApplicationNumberAndIsActiveTrue(applicationNumber);
            if (applicationOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            AdmissionApplicationDto applicationDto = convertToDto(applicationOptional.get());
            return ResponseEntity.ok(ApiResponse.success("Application retrieved successfully", applicationDto));
        } catch (Exception e) {
            log.error("Error fetching application by number: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch application: " + e.getMessage()));
        }
    }

    private AdmissionApplicationDto convertToDto(AdmissionApplication application) {
        return AdmissionApplicationDto.builder()
                .id(application.getId())
                .applicationNumber(application.getApplicationNumber())
                .firstName(application.getFirstName())
                .lastName(application.getLastName())
                .middleName(application.getMiddleName())
                .dateOfBirth(application.getDateOfBirth())
                .gender(application.getGender())
                .nationality(application.getNationality())
                .idNumber(application.getIdNumber())
                .phoneNumber(application.getPhoneNumber())
                .email(application.getEmail())
                .address(application.getAddress())
                .previousSchool(application.getPreviousSchool())
                .previousSchoolLocation(application.getPreviousSchoolLocation())
                .previousClass(application.getPreviousClass())
                .kcpeIndex(application.getKcpeIndex())
                .kcpeMarks(application.getKcpeMarks())
                .kcpeYear(application.getKcpeYear())
                .parentName(application.getParentName())
                .parentPhone(application.getParentPhone())
                .parentEmail(application.getParentEmail())
                .parentOccupation(application.getParentOccupation())
                .parentAddress(application.getParentAddress())
                .guardianName(application.getGuardianName())
                .guardianPhone(application.getGuardianPhone())
                .guardianEmail(application.getGuardianEmail())
                .guardianRelationship(application.getGuardianRelationship())
                .applyingClassId(application.getApplyingClass().getId())
                .applyingClassName(application.getApplyingClass().getName())
                .academicYearId(application.getAcademicYear().getId())
                .academicYearName(application.getAcademicYear().getName())
                .status(application.getStatus())
                .applicationDate(application.getApplicationDate())
                .reviewDate(application.getReviewDate())
                .decisionDate(application.getDecisionDate())
                .reviewNotes(application.getReviewNotes())
                .decisionReason(application.getDecisionReason())
                .applicationFee(application.getApplicationFee())
                .applicationFeePaid(application.getApplicationFeePaid())
                .isActive(application.getIsActive())
                .reviewedById(application.getReviewedBy() != null ? application.getReviewedBy().getId() : null)
                .reviewedByName(application.getReviewedBy() != null ? 
                    application.getReviewedBy().getFirstName() + " " + application.getReviewedBy().getLastName() : null)
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
