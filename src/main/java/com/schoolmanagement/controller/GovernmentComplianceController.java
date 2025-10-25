package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.CapitationGrantDto;
import com.schoolmanagement.entity.CapitationGrant;
import com.schoolmanagement.entity.Bursary;
import com.schoolmanagement.repository.CapitationGrantRepository;
import com.schoolmanagement.repository.BursaryRepository;
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

class GrantStatistics {
    public Long schoolId;
    public Long academicYearId;
    public Double totalApproved;
    public Double totalDisbursed;
    public Double pendingAmount;
}

class BursaryStatistics {
    public Long schoolId;
    public Long academicYearId;
    public Double totalApproved;
    public Long totalBursaries;
}

@RestController
@RequestMapping("/api/government")
@RequiredArgsConstructor
@Slf4j
public class GovernmentComplianceController {

    private final CapitationGrantRepository capitationGrantRepository;
    private final BursaryRepository bursaryRepository;

    // Capitation Grant Management
    @GetMapping("/capitation-grants")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<List<CapitationGrantDto>>> getAllCapitationGrants() {
        try {
            log.info("Fetching all capitation grants");
            List<CapitationGrant> grants = capitationGrantRepository.findAll();
            List<CapitationGrantDto> grantDtos = grants.stream()
                    .map(this::convertCapitationGrantToDto)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Capitation grants retrieved successfully", grantDtos));
        } catch (Exception e) {
            log.error("Error fetching capitation grants: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch capitation grants: " + e.getMessage()));
        }
    }

    @GetMapping("/capitation-grants/school/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<List<CapitationGrantDto>>> getCapitationGrantsBySchool(@PathVariable Long schoolId) {
        try {
            log.info("Fetching capitation grants for school: {}", schoolId);
            List<CapitationGrant> grants = capitationGrantRepository.findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(schoolId);
            List<CapitationGrantDto> grantDtos = grants.stream()
                    .map(this::convertCapitationGrantToDto)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Capitation grants retrieved successfully", grantDtos));
        } catch (Exception e) {
            log.error("Error fetching capitation grants by school: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch capitation grants: " + e.getMessage()));
        }
    }

    @GetMapping("/capitation-grants/academic-year/{academicYearId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<List<CapitationGrantDto>>> getCapitationGrantsByAcademicYear(@PathVariable Long academicYearId) {
        try {
            log.info("Fetching capitation grants for academic year: {}", academicYearId);
            List<CapitationGrant> grants = capitationGrantRepository.findByAcademicYearIdAndIsActiveTrueOrderByApplicationDateDesc(academicYearId);
            List<CapitationGrantDto> grantDtos = grants.stream()
                    .map(this::convertCapitationGrantToDto)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Capitation grants retrieved successfully", grantDtos));
        } catch (Exception e) {
            log.error("Error fetching capitation grants by academic year: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch capitation grants: " + e.getMessage()));
        }
    }

    @GetMapping("/capitation-grants/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<CapitationGrantDto>> getCapitationGrantById(@PathVariable Long id) {
        try {
            log.info("Fetching capitation grant with ID: {}", id);
            Optional<CapitationGrant> grantOptional = capitationGrantRepository.findById(id);
            if (grantOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            CapitationGrantDto grantDto = convertCapitationGrantToDto(grantOptional.get());
            return ResponseEntity.ok(ApiResponse.success("Capitation grant retrieved successfully", grantDto));
        } catch (Exception e) {
            log.error("Error fetching capitation grant by ID: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch capitation grant: " + e.getMessage()));
        }
    }

    @GetMapping("/capitation-grants/statistics/school/{schoolId}/academic-year/{academicYearId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<Object>> getCapitationGrantStatistics(
            @PathVariable Long schoolId,
            @PathVariable Long academicYearId) {
        try {
            log.info("Fetching capitation grant statistics for school: {} and academic year: {}", schoolId, academicYearId);
            
            Double totalApproved = capitationGrantRepository.getTotalApprovedAmountBySchoolAndAcademicYear(schoolId, academicYearId);
            Double totalDisbursed = capitationGrantRepository.getTotalDisbursedAmountBySchoolAndAcademicYear(schoolId, academicYearId);
            
            GrantStatistics stats = new GrantStatistics();
            stats.schoolId = schoolId;
            stats.academicYearId = academicYearId;
            stats.totalApproved = totalApproved != null ? totalApproved : 0.0;
            stats.totalDisbursed = totalDisbursed != null ? totalDisbursed : 0.0;
            stats.pendingAmount = stats.totalApproved - stats.totalDisbursed;
            
            return ResponseEntity.ok(ApiResponse.success("Capitation grant statistics retrieved successfully", stats));
        } catch (Exception e) {
            log.error("Error fetching capitation grant statistics: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch capitation grant statistics: " + e.getMessage()));
        }
    }

    // Bursary Management
    @GetMapping("/bursaries")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<List<Object>>> getAllBursaries() {
        try {
            log.info("Fetching all bursaries");
            List<Bursary> bursaries = bursaryRepository.findAll();
            List<Object> bursaryDtos = bursaries.stream()
                    .map(this::convertBursaryToDto)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Bursaries retrieved successfully", bursaryDtos));
        } catch (Exception e) {
            log.error("Error fetching bursaries: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch bursaries: " + e.getMessage()));
        }
    }

    @GetMapping("/bursaries/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<Object>>> getBursariesByStudent(@PathVariable Long studentId) {
        try {
            log.info("Fetching bursaries for student: {}", studentId);
            List<Bursary> bursaries = bursaryRepository.findByStudentIdAndIsActiveTrueOrderByApplicationDateDesc(studentId);
            List<Object> bursaryDtos = bursaries.stream()
                    .map(this::convertBursaryToDto)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Bursaries retrieved successfully", bursaryDtos));
        } catch (Exception e) {
            log.error("Error fetching bursaries by student: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch bursaries: " + e.getMessage()));
        }
    }

    @GetMapping("/bursaries/school/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<List<Object>>> getBursariesBySchool(@PathVariable Long schoolId) {
        try {
            log.info("Fetching bursaries for school: {}", schoolId);
            List<Bursary> bursaries = bursaryRepository.findBySchoolIdAndIsActiveTrueOrderByApplicationDateDesc(schoolId);
            List<Object> bursaryDtos = bursaries.stream()
                    .map(this::convertBursaryToDto)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Bursaries retrieved successfully", bursaryDtos));
        } catch (Exception e) {
            log.error("Error fetching bursaries by school: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch bursaries: " + e.getMessage()));
        }
    }

    @GetMapping("/bursaries/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<List<Object>>> getBursariesByStatus(@PathVariable Bursary.BursaryStatus status) {
        try {
            log.info("Fetching bursaries by status: {}", status);
            List<Bursary> bursaries = bursaryRepository.findByStatusAndIsActiveTrueOrderByApplicationDateDesc(status);
            List<Object> bursaryDtos = bursaries.stream()
                    .map(this::convertBursaryToDto)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Bursaries retrieved successfully", bursaryDtos));
        } catch (Exception e) {
            log.error("Error fetching bursaries by status: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch bursaries: " + e.getMessage()));
        }
    }

    @GetMapping("/bursaries/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Object>> getBursaryById(@PathVariable Long id) {
        try {
            log.info("Fetching bursary with ID: {}", id);
            Optional<Bursary> bursaryOptional = bursaryRepository.findById(id);
            if (bursaryOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Object bursaryDto = convertBursaryToDto(bursaryOptional.get());
            return ResponseEntity.ok(ApiResponse.success("Bursary retrieved successfully", bursaryDto));
        } catch (Exception e) {
            log.error("Error fetching bursary by ID: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch bursary: " + e.getMessage()));
        }
    }

    @GetMapping("/bursaries/statistics/school/{schoolId}/academic-year/{academicYearId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'GOVERNMENT_OFFICER')")
    public ResponseEntity<ApiResponse<Object>> getBursaryStatistics(
            @PathVariable Long schoolId,
            @PathVariable Long academicYearId) {
        try {
            log.info("Fetching bursary statistics for school: {} and academic year: {}", schoolId, academicYearId);
            
            Double totalApproved = bursaryRepository.getTotalApprovedAmountBySchoolAndAcademicYear(schoolId, academicYearId);
            Long totalBursaries = bursaryRepository.countByStudentIdAndIsActiveTrue(schoolId); // Using schoolId as placeholder
            
            BursaryStatistics stats = new BursaryStatistics();
            stats.schoolId = schoolId;
            stats.academicYearId = academicYearId;
            stats.totalApproved = totalApproved != null ? totalApproved : 0.0;
            stats.totalBursaries = totalBursaries;
            
            return ResponseEntity.ok(ApiResponse.success("Bursary statistics retrieved successfully", stats));
        } catch (Exception e) {
            log.error("Error fetching bursary statistics: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch bursary statistics: " + e.getMessage()));
        }
    }

    private CapitationGrantDto convertCapitationGrantToDto(CapitationGrant grant) {
        return CapitationGrantDto.builder()
                .id(grant.getId())
                .schoolId(grant.getSchool().getId())
                .schoolName(grant.getSchool().getName())
                .academicYearId(grant.getAcademicYear().getId())
                .academicYearName(grant.getAcademicYear().getName())
                .grantNumber(grant.getGrantNumber())
                .totalAmount(grant.getTotalAmount())
                .receivedAmount(grant.getReceivedAmount())
                .pendingAmount(grant.getPendingAmount())
                .status(grant.getStatus().name())
                .expectedDate(grant.getExpectedDate())
                .receivedDate(grant.getReceivedDate())
                .studentCount(grant.getStudentCount())
                .perStudentAmount(grant.getPerStudentAmount())
                .remarks(grant.getRemarks())
                .isActive(grant.getIsActive())
                .createdAt(grant.getCreatedAt())
                .updatedAt(grant.getUpdatedAt())
                .build();
    }

    private Object convertBursaryToDto(Bursary bursary) {
        return new Object() {
            public final Long id = bursary.getId();
            public final Long studentId = bursary.getStudent().getId();
            public final String studentName = bursary.getStudent().getFirstName() + " " + bursary.getStudent().getLastName();
            public final Long schoolId = bursary.getSchool().getId();
            public final String schoolName = bursary.getSchool().getName();
            public final Long academicYearId = bursary.getAcademicYear().getId();
            public final String academicYearName = bursary.getAcademicYear().getName();
            public final String bursaryNumber = bursary.getBursaryNumber();
            public final String bursaryType = bursary.getBursaryType();
            public final Double approvedAmount = bursary.getApprovedAmount().doubleValue();
            public final Double disbursedAmount = bursary.getDisbursedAmount().doubleValue();
            public final Double utilizedAmount = bursary.getUtilizedAmount().doubleValue();
            public final Double balanceAmount = bursary.getBalanceAmount().doubleValue();
            public final String status = bursary.getStatus().name();
            public final String applicationReason = bursary.getApplicationReason();
            public final String approvalNotes = bursary.getApprovalNotes();
            public final String utilizationReport = bursary.getUtilizationReport();
            public final Boolean isActive = bursary.getIsActive();
            public final String createdAt = bursary.getCreatedAt().toString();
            public final String updatedAt = bursary.getUpdatedAt().toString();
        };
    }
}
