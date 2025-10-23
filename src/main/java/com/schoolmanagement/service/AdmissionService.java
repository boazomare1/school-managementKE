package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.AdmissionApplication;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.AdmissionApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

class AdmissionStatistics {
    public long totalApplications;
    public long pendingCount;
    public long acceptedCount;
    public long rejectedCount;
    public long academicYearId;
}

@Service
@RequiredArgsConstructor
@Slf4j
public class AdmissionService {

    private final AdmissionApplicationRepository admissionApplicationRepository;

    @Transactional
    public ApiResponse<AdmissionApplication> submitApplication(AdmissionApplication application) {
        try {
            log.info("Submitting admission application for: {} {}", application.getFirstName(), application.getLastName());
            
            // Generate application number
            String applicationNumber = "APP-" + System.currentTimeMillis();
            application.setApplicationNumber(applicationNumber);
            application.setStatus(AdmissionApplication.ApplicationStatus.PENDING);
            application.setIsActive(true);

            AdmissionApplication savedApplication = admissionApplicationRepository.save(application);
            return ApiResponse.success("Application submitted successfully", savedApplication);

        } catch (Exception e) {
            log.error("Error submitting application: {}", e.getMessage());
            return ApiResponse.error("Failed to submit application: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<AdmissionApplication> reviewApplication(Long applicationId, String reviewNotes, User reviewer) {
        try {
            log.info("Reviewing application: {} by user: {}", applicationId, reviewer.getUsername());
            
            Optional<AdmissionApplication> applicationOptional = admissionApplicationRepository.findById(applicationId);
            if (applicationOptional.isEmpty()) {
                return ApiResponse.error("Application not found");
            }

            AdmissionApplication application = applicationOptional.get();
            application.setReviewNotes(reviewNotes);
            application.setReviewedBy(reviewer);
            application.setReviewDate(java.time.LocalDate.now());

            AdmissionApplication savedApplication = admissionApplicationRepository.save(application);
            return ApiResponse.success("Application review completed", savedApplication);

        } catch (Exception e) {
            log.error("Error reviewing application: {}", e.getMessage());
            return ApiResponse.error("Failed to review application: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<AdmissionApplication> makeDecision(Long applicationId, AdmissionApplication.ApplicationStatus decision, String decisionReason, User decisionMaker) {
        try {
            log.info("Making decision for application: {} - {}", applicationId, decision);
            
            Optional<AdmissionApplication> applicationOptional = admissionApplicationRepository.findById(applicationId);
            if (applicationOptional.isEmpty()) {
                return ApiResponse.error("Application not found");
            }

            AdmissionApplication application = applicationOptional.get();
            application.setStatus(decision);
            application.setDecisionReason(decisionReason);
            application.setDecisionDate(java.time.LocalDate.now());

            AdmissionApplication savedApplication = admissionApplicationRepository.save(application);
            return ApiResponse.success("Application decision made", savedApplication);

        } catch (Exception e) {
            log.error("Error making application decision: {}", e.getMessage());
            return ApiResponse.error("Failed to make application decision: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<User> enrollAcceptedStudent(Long applicationId, User enroller) {
        try {
            log.info("Enrolling accepted student from application: {}", applicationId);
            
            Optional<AdmissionApplication> applicationOptional = admissionApplicationRepository.findById(applicationId);
            if (applicationOptional.isEmpty()) {
                return ApiResponse.error("Application not found");
            }

            AdmissionApplication application = applicationOptional.get();
            if (application.getStatus() != AdmissionApplication.ApplicationStatus.ACCEPTED) {
                return ApiResponse.error("Application is not in ACCEPTED status");
            }

            // Create user account for the student (simplified)
            User student = User.builder()
                    .username(application.getFirstName().toLowerCase() + application.getLastName().toLowerCase())
                    .email(application.getEmail())
                    .firstName(application.getFirstName())
                    .lastName(application.getLastName())
                    .phoneNumber(application.getPhoneNumber())
                    .address(application.getAddress())
                    .isActive(true)
                    .build();

            return ApiResponse.success("Student enrolled successfully", student);

        } catch (Exception e) {
            log.error("Error enrolling student: {}", e.getMessage());
            return ApiResponse.error("Failed to enroll student: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<AdmissionApplication>> getApplicationsByStatus(AdmissionApplication.ApplicationStatus status) {
        try {
            log.info("Fetching applications with status: {}", status);
            List<AdmissionApplication> applications = admissionApplicationRepository.findByStatusAndIsActiveTrueOrderByApplicationDateDesc(status);
            return ApiResponse.success("Applications retrieved successfully", applications);
        } catch (Exception e) {
            log.error("Error fetching applications: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch applications: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<AdmissionApplication>> getApplicationsByClass(Long classId) {
        try {
            log.info("Fetching applications for class: {}", classId);
            List<AdmissionApplication> applications = admissionApplicationRepository.findByApplyingClassIdAndIsActiveTrueOrderByApplicationDateDesc(classId);
            return ApiResponse.success("Applications retrieved successfully", applications);
        } catch (Exception e) {
            log.error("Error fetching applications by class: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch applications: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<Object> getAdmissionStatistics(Long academicYearId) {
        try {
            log.info("Fetching admission statistics for academic year: {}", academicYearId);
            
            List<AdmissionApplication> allApplications = admissionApplicationRepository.findByAcademicYearIdAndIsActiveTrueOrderByApplicationDateDesc(academicYearId);
            
            long totalApplications = allApplications.size();
            long pendingCount = allApplications.stream().filter(app -> app.getStatus() == AdmissionApplication.ApplicationStatus.PENDING).count();
            long acceptedCount = allApplications.stream().filter(app -> app.getStatus() == AdmissionApplication.ApplicationStatus.ACCEPTED).count();
            long rejectedCount = allApplications.stream().filter(app -> app.getStatus() == AdmissionApplication.ApplicationStatus.REJECTED).count();
            
            AdmissionStatistics stats = new AdmissionStatistics();
            stats.totalApplications = totalApplications;
            stats.pendingCount = pendingCount;
            stats.acceptedCount = acceptedCount;
            stats.rejectedCount = rejectedCount;
            stats.academicYearId = academicYearId;
            
            return ApiResponse.success("Admission statistics retrieved successfully", stats);
        } catch (Exception e) {
            log.error("Error fetching admission statistics: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch admission statistics: " + e.getMessage());
        }
    }
}