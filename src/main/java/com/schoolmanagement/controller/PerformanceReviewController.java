package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.PerformanceReview;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.PerformanceReviewRepository;
import com.schoolmanagement.repository.SupportStaffRepository;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class RatingSummary {
    public Long staffId;
    public Double averageRating;
    public Long reviewCount;
}

@RestController
@RequestMapping("/api/performance-reviews")
@RequiredArgsConstructor
@Slf4j
public class PerformanceReviewController {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final SupportStaffRepository supportStaffRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<PerformanceReview>> createPerformanceReview(
            @RequestBody PerformanceReview performanceReview,
            Authentication authentication) {
        try {
            log.info("Creating performance review for staff: {}", performanceReview.getSupportStaff().getId());

            // Set reviewer
            User user = (User) authentication.getPrincipal();
            performanceReview.setReviewer(user);
            performanceReview.setReviewDate(LocalDate.now());
            performanceReview.setStatus(PerformanceReview.ReviewStatus.DRAFT);
            performanceReview.setIsActive(true);

            PerformanceReview savedReview = performanceReviewRepository.save(performanceReview);
            return ResponseEntity.ok(ApiResponse.success("Performance review created successfully", savedReview));

        } catch (Exception e) {
            log.error("Error creating performance review: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to create performance review: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<PerformanceReview>> updatePerformanceReview(
            @PathVariable Long id,
            @RequestBody PerformanceReview performanceReview,
            Authentication authentication) {
        try {
            log.info("Updating performance review: {}", id);

            Optional<PerformanceReview> reviewOptional = performanceReviewRepository.findById(id);
            if (reviewOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            PerformanceReview existingReview = reviewOptional.get();
            existingReview.setOverallRating(performanceReview.getOverallRating());
            existingReview.setTeachingRating(performanceReview.getTeachingRating());
            existingReview.setPunctualityRating(performanceReview.getPunctualityRating());
            existingReview.setProfessionalismRating(performanceReview.getProfessionalismRating());
            existingReview.setStudentEngagementRating(performanceReview.getStudentEngagementRating());
            existingReview.setStrengths(performanceReview.getStrengths());
            existingReview.setAreasForImprovement(performanceReview.getAreasForImprovement());
            existingReview.setGoals(performanceReview.getGoals());
            existingReview.setRecommendations(performanceReview.getRecommendations());
            existingReview.setComments(performanceReview.getComments());
            existingReview.setStatus(performanceReview.getStatus());

            PerformanceReview savedReview = performanceReviewRepository.save(existingReview);
            return ResponseEntity.ok(ApiResponse.success("Performance review updated successfully", savedReview));

        } catch (Exception e) {
            log.error("Error updating performance review: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to update performance review: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<PerformanceReview>> completePerformanceReview(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            log.info("Completing performance review: {}", id);

            Optional<PerformanceReview> reviewOptional = performanceReviewRepository.findById(id);
            if (reviewOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            PerformanceReview review = reviewOptional.get();
            review.setStatus(PerformanceReview.ReviewStatus.COMPLETED);
            User user = (User) authentication.getPrincipal();
            review.setReviewer(user);

            PerformanceReview savedReview = performanceReviewRepository.save(review);
            return ResponseEntity.ok(ApiResponse.success("Performance review completed successfully", savedReview));

        } catch (Exception e) {
            log.error("Error completing performance review: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to complete performance review: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<PerformanceReview>> approvePerformanceReview(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            log.info("Approving performance review: {}", id);

            Optional<PerformanceReview> reviewOptional = performanceReviewRepository.findById(id);
            if (reviewOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            PerformanceReview review = reviewOptional.get();
            review.setStatus(PerformanceReview.ReviewStatus.APPROVED);

            PerformanceReview savedReview = performanceReviewRepository.save(review);
            return ResponseEntity.ok(ApiResponse.success("Performance review approved successfully", savedReview));

        } catch (Exception e) {
            log.error("Error approving performance review: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to approve performance review: " + e.getMessage()));
        }
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<PerformanceReview>>> getStaffPerformanceReviews(@PathVariable Long staffId) {
        try {
            log.info("Fetching performance reviews for staff: {}", staffId);
            List<PerformanceReview> reviews = performanceReviewRepository.findBySupportStaffIdAndIsActiveTrueOrderByReviewDateDesc(staffId);
            return ResponseEntity.ok(ApiResponse.success("Performance reviews retrieved successfully", reviews));
        } catch (Exception e) {
            log.error("Error fetching staff performance reviews: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch performance reviews: " + e.getMessage()));
        }
    }

    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<PerformanceReview>>> getReviewsByReviewer(@PathVariable Long reviewerId) {
        try {
            log.info("Fetching performance reviews by reviewer: {}", reviewerId);
            List<PerformanceReview> reviews = performanceReviewRepository.findByReviewerIdAndIsActiveTrueOrderByReviewDateDesc(reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Performance reviews retrieved successfully", reviews));
        } catch (Exception e) {
            log.error("Error fetching reviews by reviewer: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch performance reviews: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<PerformanceReview>>> getReviewsByStatus(@PathVariable PerformanceReview.ReviewStatus status) {
        try {
            log.info("Fetching performance reviews by status: {}", status);
            List<PerformanceReview> reviews = performanceReviewRepository.findByStatusAndIsActiveTrueOrderByReviewDateDesc(status);
            return ResponseEntity.ok(ApiResponse.success("Performance reviews retrieved successfully", reviews));
        } catch (Exception e) {
            log.error("Error fetching reviews by status: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch performance reviews: " + e.getMessage()));
        }
    }

    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<PerformanceReview>>> getReviewsByPeriod(
            @RequestParam Long staffId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            log.info("Fetching performance reviews for staff: {} from {} to {}", staffId, startDate, endDate);
            List<PerformanceReview> reviews = performanceReviewRepository.findBySupportStaffIdAndReviewPeriodBetweenAndIsActiveTrueOrderByReviewDateDesc(staffId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Performance reviews retrieved successfully", reviews));
        } catch (Exception e) {
            log.error("Error fetching reviews by period: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch performance reviews: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR', 'TEACHER')")
    public ResponseEntity<ApiResponse<PerformanceReview>> getPerformanceReviewById(@PathVariable Long id) {
        try {
            log.info("Fetching performance review with ID: {}", id);
            Optional<PerformanceReview> reviewOptional = performanceReviewRepository.findById(id);
            if (reviewOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ApiResponse.success("Performance review retrieved successfully", reviewOptional.get()));
        } catch (Exception e) {
            log.error("Error fetching performance review by ID: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch performance review: " + e.getMessage()));
        }
    }

    @GetMapping("/staff/{staffId}/average-rating")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'SUPERVISOR', 'TEACHER')")
    public ResponseEntity<ApiResponse<Object>> getStaffAverageRating(@PathVariable Long staffId) {
        try {
            log.info("Fetching average rating for staff: {}", staffId);
            Double averageRating = performanceReviewRepository.getAverageRatingBySupportStaffId(staffId);
            Long reviewCount = performanceReviewRepository.countBySupportStaffIdAndIsActiveTrue(staffId);
            
            RatingSummary summary = new RatingSummary();
            summary.staffId = staffId;
            summary.averageRating = averageRating;
            summary.reviewCount = reviewCount;
            
            return ResponseEntity.ok(ApiResponse.success("Average rating retrieved successfully", summary));
        } catch (Exception e) {
            log.error("Error fetching average rating: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch average rating: " + e.getMessage()));
        }
    }
}
