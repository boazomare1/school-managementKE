package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.NamingSeriesDto;
import com.schoolmanagement.entity.NamingSeries;
import com.schoolmanagement.service.NamingSeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/naming-series")
@RequiredArgsConstructor
@Slf4j
public class NamingSeriesController {
    
    private final NamingSeriesService namingSeriesService;
    
    // Create a new naming series
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<NamingSeriesDto>> createNamingSeries(@Valid @RequestBody NamingSeriesDto namingSeriesDto) {
        log.info("Creating naming series: {}", namingSeriesDto.getName());
        return ResponseEntity.ok(namingSeriesService.createNamingSeries(namingSeriesDto));
    }
    
    // Get all naming series
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<NamingSeriesDto>>> getAllNamingSeries() {
        log.info("Fetching all naming series");
        return ResponseEntity.ok(namingSeriesService.getAllNamingSeries());
    }
    
    // Get naming series by type
    @GetMapping("/type/{seriesType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<NamingSeriesDto>>> getNamingSeriesByType(@PathVariable NamingSeries.SeriesType seriesType) {
        log.info("Fetching naming series by type: {}", seriesType);
        return ResponseEntity.ok(namingSeriesService.getNamingSeriesByType(seriesType));
    }
    
    // Get default naming series for a type
    @GetMapping("/default/{seriesType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<NamingSeriesDto>> getDefaultNamingSeries(@PathVariable NamingSeries.SeriesType seriesType) {
        log.info("Fetching default naming series for type: {}", seriesType);
        return ResponseEntity.ok(namingSeriesService.getDefaultNamingSeries(seriesType));
    }
    
    // Generate next ID for a series
    @PostMapping("/generate/{seriesName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<String>> generateNextId(@PathVariable String seriesName) {
        log.info("Generating next ID for series: {}", seriesName);
        return ResponseEntity.ok(namingSeriesService.generateNextId(seriesName));
    }
    
    // Generate next ID for a series type
    @PostMapping("/generate-by-type/{seriesType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<String>> generateNextIdByType(@PathVariable NamingSeries.SeriesType seriesType) {
        log.info("Generating next ID for series type: {}", seriesType);
        return ResponseEntity.ok(namingSeriesService.generateNextIdByType(seriesType));
    }
    
    // Update naming series
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<NamingSeriesDto>> updateNamingSeries(
            @PathVariable Long id, 
            @Valid @RequestBody NamingSeriesDto namingSeriesDto) {
        log.info("Updating naming series with ID: {}", id);
        return ResponseEntity.ok(namingSeriesService.updateNamingSeries(id, namingSeriesDto));
    }
    
    // Deactivate naming series
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<Void>> deactivateNamingSeries(@PathVariable Long id) {
        log.info("Deactivating naming series with ID: {}", id);
        return ResponseEntity.ok(namingSeriesService.deactivateNamingSeries(id));
    }
    
    // Search naming series
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<NamingSeriesDto>>> searchNamingSeries(@RequestParam String q) {
        log.info("Searching naming series with term: {}", q);
        return ResponseEntity.ok(namingSeriesService.searchNamingSeries(q));
    }
    
    // Reset naming series
    @PostMapping("/{id}/reset")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<Void>> resetNamingSeries(@PathVariable Long id) {
        log.info("Resetting naming series with ID: {}", id);
        return ResponseEntity.ok(namingSeriesService.resetNamingSeries(id));
    }
}
