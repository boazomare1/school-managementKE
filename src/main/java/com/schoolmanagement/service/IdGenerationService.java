package com.schoolmanagement.service;

import com.schoolmanagement.entity.NamingSeries;
import com.schoolmanagement.repository.NamingSeriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IdGenerationService {
    
    private final NamingSeriesRepository namingSeriesRepository;
    
    /**
     * Generate next employee ID using default series
     */
    public String generateEmployeeId() {
        return generateNextIdByType(NamingSeries.SeriesType.EMPLOYEE);
    }
    
    /**
     * Generate next student admission number using default series
     */
    public String generateStudentAdmissionNumber() {
        return generateNextIdByType(NamingSeries.SeriesType.STUDENT);
    }
    
    /**
     * Generate next teacher ID using default series
     */
    public String generateTeacherId() {
        return generateNextIdByType(NamingSeries.SeriesType.TEACHER);
    }
    
    /**
     * Generate next support staff ID using default series
     */
    public String generateSupportStaffId() {
        return generateNextIdByType(NamingSeries.SeriesType.SUPPORT_STAFF);
    }
    
    /**
     * Generate next parent ID using default series
     */
    public String generateParentId() {
        return generateNextIdByType(NamingSeries.SeriesType.PARENT);
    }
    
    /**
     * Generate next ID using a specific series name
     */
    public String generateNextIdBySeriesName(String seriesName) {
        try {
            log.info("Generating next ID for series: {}", seriesName);
            NamingSeries series = namingSeriesRepository.findByNameAndIsActiveTrue(seriesName)
                    .orElseThrow(() -> new RuntimeException("Naming series not found: " + seriesName));
            
            String nextId = series.generateNextId();
            namingSeriesRepository.save(series); // Save the updated current number
            
            log.info("Successfully generated next ID: {}", nextId);
            return nextId;
            
        } catch (Exception e) {
            log.error("Error generating next ID for series {}: {}", seriesName, e.getMessage());
            throw new RuntimeException("Failed to generate next ID: " + e.getMessage());
        }
    }
    
    /**
     * Generate next ID using series type (uses default series for that type)
     */
    public String generateNextIdByType(NamingSeries.SeriesType seriesType) {
        try {
            log.info("Generating next ID for series type: {}", seriesType);
            NamingSeries series = namingSeriesRepository.findBySeriesTypeAndIsDefaultTrueAndIsActiveTrue(seriesType)
                    .orElseThrow(() -> new RuntimeException("No default naming series found for type: " + seriesType));
            
            String nextId = series.generateNextId();
            namingSeriesRepository.save(series); // Save the updated current number
            
            log.info("Successfully generated next ID: {}", nextId);
            return nextId;
            
        } catch (Exception e) {
            log.error("Error generating next ID for series type {}: {}", seriesType, e.getMessage());
            throw new RuntimeException("Failed to generate next ID: " + e.getMessage());
        }
    }
    
    /**
     * Get the next ID without updating the counter
     */
    public String getNextIdByType(NamingSeries.SeriesType seriesType) {
        try {
            log.info("Getting next ID for series type: {}", seriesType);
            NamingSeries series = namingSeriesRepository.findBySeriesTypeAndIsDefaultTrueAndIsActiveTrue(seriesType)
                    .orElseThrow(() -> new RuntimeException("No default naming series found for type: " + seriesType));
            
            return series.getNextId();
            
        } catch (Exception e) {
            log.error("Error getting next ID for series type {}: {}", seriesType, e.getMessage());
            throw new RuntimeException("Failed to get next ID: " + e.getMessage());
        }
    }
    
    /**
     * Get the next ID by series name without updating the counter
     */
    public String getNextIdBySeriesName(String seriesName) {
        try {
            log.info("Getting next ID for series: {}", seriesName);
            NamingSeries series = namingSeriesRepository.findByNameAndIsActiveTrue(seriesName)
                    .orElseThrow(() -> new RuntimeException("Naming series not found: " + seriesName));
            
            return series.getNextId();
            
        } catch (Exception e) {
            log.error("Error getting next ID for series {}: {}", seriesName, e.getMessage());
            throw new RuntimeException("Failed to get next ID: " + e.getMessage());
        }
    }
    
    /**
     * Validate if an ID follows the expected format for a series
     */
    public boolean validateIdFormat(String id, String seriesName) {
        try {
            NamingSeries series = namingSeriesRepository.findByNameAndIsActiveTrue(seriesName)
                    .orElseThrow(() -> new RuntimeException("Naming series not found: " + seriesName));
            
            String expectedPrefix = series.getPrefix() + series.getSeparator();
            return id.startsWith(expectedPrefix);
            
        } catch (Exception e) {
            log.error("Error validating ID format: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all available series for a specific type
     */
    public java.util.List<NamingSeries> getAvailableSeriesForType(NamingSeries.SeriesType seriesType) {
        return namingSeriesRepository.findBySeriesTypeAndIsActiveTrueOrderByNameAsc(seriesType);
    }
}
