package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.NamingSeriesDto;
import com.schoolmanagement.entity.NamingSeries;
import com.schoolmanagement.repository.NamingSeriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NamingSeriesService {
    
    private final NamingSeriesRepository namingSeriesRepository;
    
    // Create a new naming series
    public ApiResponse<NamingSeriesDto> createNamingSeries(NamingSeriesDto namingSeriesDto) {
        try {
            log.info("Creating naming series: {}", namingSeriesDto.getName());
            
            // Check if name already exists
            if (namingSeriesRepository.findByNameAndIsActiveTrue(namingSeriesDto.getName()).isPresent()) {
                return ApiResponse.error("Naming series with name '" + namingSeriesDto.getName() + "' already exists");
            }
            
            // If this is set as default, unset other defaults for the same series type
            if (namingSeriesDto.getIsDefault()) {
                unsetDefaultForSeriesType(namingSeriesDto.getSeriesType());
            }
            
            NamingSeries namingSeries = NamingSeries.builder()
                    .name(namingSeriesDto.getName())
                    .description(namingSeriesDto.getDescription())
                    .prefix(namingSeriesDto.getPrefix())
                    .suffix(namingSeriesDto.getSuffix())
                    .startNumber(namingSeriesDto.getStartNumber())
                    .currentNumber(namingSeriesDto.getCurrentNumber())
                    .padding(namingSeriesDto.getPadding())
                    .separator(namingSeriesDto.getSeparator())
                    .isActive(namingSeriesDto.getIsActive())
                    .isDefault(namingSeriesDto.getIsDefault())
                    .seriesType(namingSeriesDto.getSeriesType())
                    .notes(namingSeriesDto.getNotes())
                    .build();
            
            NamingSeries savedSeries = namingSeriesRepository.save(namingSeries);
            log.info("Successfully created naming series with ID: {}", savedSeries.getId());
            
            return ApiResponse.success("Naming series created successfully", convertToDto(savedSeries));
            
        } catch (Exception e) {
            log.error("Error creating naming series: {}", e.getMessage());
            return ApiResponse.error("Failed to create naming series: " + e.getMessage());
        }
    }
    
    // Get all naming series
    public ApiResponse<List<NamingSeriesDto>> getAllNamingSeries() {
        try {
            log.info("Fetching all naming series");
            List<NamingSeries> series = namingSeriesRepository.findByIsActiveTrueOrderByNameAsc();
            List<NamingSeriesDto> dtoList = series.stream().map(this::convertToDto).collect(Collectors.toList());
            return ApiResponse.success("Naming series retrieved successfully", dtoList);
        } catch (Exception e) {
            log.error("Error fetching naming series: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch naming series: " + e.getMessage());
        }
    }
    
    // Get naming series by type
    public ApiResponse<List<NamingSeriesDto>> getNamingSeriesByType(NamingSeries.SeriesType seriesType) {
        try {
            log.info("Fetching naming series by type: {}", seriesType);
            List<NamingSeries> series = namingSeriesRepository.findBySeriesTypeOrderByDefaultAndName(seriesType);
            List<NamingSeriesDto> dtoList = series.stream().map(this::convertToDto).collect(Collectors.toList());
            return ApiResponse.success("Naming series retrieved successfully", dtoList);
        } catch (Exception e) {
            log.error("Error fetching naming series by type: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch naming series: " + e.getMessage());
        }
    }
    
    // Get default naming series for a type
    public ApiResponse<NamingSeriesDto> getDefaultNamingSeries(NamingSeries.SeriesType seriesType) {
        try {
            log.info("Fetching default naming series for type: {}", seriesType);
            NamingSeries series = namingSeriesRepository.findBySeriesTypeAndIsDefaultTrueAndIsActiveTrue(seriesType)
                    .orElse(null);
            
            if (series == null) {
                return ApiResponse.error("No default naming series found for type: " + seriesType);
            }
            
            return ApiResponse.success("Default naming series retrieved successfully", convertToDto(series));
            
        } catch (Exception e) {
            log.error("Error fetching default naming series: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch default naming series: " + e.getMessage());
        }
    }
    
    // Generate next ID for a series
    public ApiResponse<String> generateNextId(String seriesName) {
        try {
            log.info("Generating next ID for series: {}", seriesName);
            NamingSeries series = namingSeriesRepository.findByNameAndIsActiveTrue(seriesName)
                    .orElseThrow(() -> new RuntimeException("Naming series not found: " + seriesName));
            
            String nextId = series.generateNextId();
            namingSeriesRepository.save(series); // Save the updated current number
            
            log.info("Successfully generated next ID: {}", nextId);
            return ApiResponse.success("Next ID generated successfully", nextId);
            
        } catch (Exception e) {
            log.error("Error generating next ID: {}", e.getMessage());
            return ApiResponse.error("Failed to generate next ID: " + e.getMessage());
        }
    }
    
    // Generate next ID for a series type (using default series)
    public ApiResponse<String> generateNextIdByType(NamingSeries.SeriesType seriesType) {
        try {
            log.info("Generating next ID for series type: {}", seriesType);
            NamingSeries series = namingSeriesRepository.findBySeriesTypeAndIsDefaultTrueAndIsActiveTrue(seriesType)
                    .orElseThrow(() -> new RuntimeException("No default naming series found for type: " + seriesType));
            
            String nextId = series.generateNextId();
            namingSeriesRepository.save(series); // Save the updated current number
            
            log.info("Successfully generated next ID: {}", nextId);
            return ApiResponse.success("Next ID generated successfully", nextId);
            
        } catch (Exception e) {
            log.error("Error generating next ID by type: {}", e.getMessage());
            return ApiResponse.error("Failed to generate next ID: " + e.getMessage());
        }
    }
    
    // Update naming series
    public ApiResponse<NamingSeriesDto> updateNamingSeries(Long id, NamingSeriesDto namingSeriesDto) {
        try {
            log.info("Updating naming series with ID: {}", id);
            NamingSeries existingSeries = namingSeriesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Naming series not found"));
            
            // Check if name is being changed and if new name already exists
            if (!existingSeries.getName().equals(namingSeriesDto.getName())) {
                if (namingSeriesRepository.findByNameAndIsActiveTrue(namingSeriesDto.getName()).isPresent()) {
                    return ApiResponse.error("Naming series with name '" + namingSeriesDto.getName() + "' already exists");
                }
            }
            
            // If this is set as default, unset other defaults for the same series type
            if (namingSeriesDto.getIsDefault() && !existingSeries.getIsDefault()) {
                unsetDefaultForSeriesType(namingSeriesDto.getSeriesType());
            }
            
            existingSeries.setName(namingSeriesDto.getName());
            existingSeries.setDescription(namingSeriesDto.getDescription());
            existingSeries.setPrefix(namingSeriesDto.getPrefix());
            existingSeries.setSuffix(namingSeriesDto.getSuffix());
            existingSeries.setStartNumber(namingSeriesDto.getStartNumber());
            existingSeries.setCurrentNumber(namingSeriesDto.getCurrentNumber());
            existingSeries.setPadding(namingSeriesDto.getPadding());
            existingSeries.setSeparator(namingSeriesDto.getSeparator());
            existingSeries.setIsActive(namingSeriesDto.getIsActive());
            existingSeries.setIsDefault(namingSeriesDto.getIsDefault());
            existingSeries.setSeriesType(namingSeriesDto.getSeriesType());
            existingSeries.setNotes(namingSeriesDto.getNotes());
            
            NamingSeries updatedSeries = namingSeriesRepository.save(existingSeries);
            log.info("Successfully updated naming series with ID: {}", updatedSeries.getId());
            
            return ApiResponse.success("Naming series updated successfully", convertToDto(updatedSeries));
            
        } catch (Exception e) {
            log.error("Error updating naming series: {}", e.getMessage());
            return ApiResponse.error("Failed to update naming series: " + e.getMessage());
        }
    }
    
    // Deactivate naming series
    public ApiResponse<Void> deactivateNamingSeries(Long id) {
        try {
            log.info("Deactivating naming series with ID: {}", id);
            NamingSeries series = namingSeriesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Naming series not found"));
            
            series.setIsActive(false);
            namingSeriesRepository.save(series);
            log.info("Successfully deactivated naming series with ID: {}", id);
            
            return ApiResponse.success("Naming series deactivated successfully", null);
            
        } catch (Exception e) {
            log.error("Error deactivating naming series: {}", e.getMessage());
            return ApiResponse.error("Failed to deactivate naming series: " + e.getMessage());
        }
    }
    
    // Search naming series
    public ApiResponse<List<NamingSeriesDto>> searchNamingSeries(String searchTerm) {
        try {
            log.info("Searching naming series with term: {}", searchTerm);
            List<NamingSeries> series = namingSeriesRepository.findBySearchTerm(searchTerm);
            List<NamingSeriesDto> dtoList = series.stream().map(this::convertToDto).collect(Collectors.toList());
            return ApiResponse.success("Search results retrieved successfully", dtoList);
        } catch (Exception e) {
            log.error("Error searching naming series: {}", e.getMessage());
            return ApiResponse.error("Failed to search naming series: " + e.getMessage());
        }
    }
    
    // Reset naming series
    public ApiResponse<Void> resetNamingSeries(Long id) {
        try {
            log.info("Resetting naming series with ID: {}", id);
            NamingSeries series = namingSeriesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Naming series not found"));
            
            series.resetSeries();
            namingSeriesRepository.save(series);
            log.info("Successfully reset naming series with ID: {}", id);
            
            return ApiResponse.success("Naming series reset successfully", null);
            
        } catch (Exception e) {
            log.error("Error resetting naming series: {}", e.getMessage());
            return ApiResponse.error("Failed to reset naming series: " + e.getMessage());
        }
    }
    
    // Helper method to unset default for a series type
    private void unsetDefaultForSeriesType(NamingSeries.SeriesType seriesType) {
        List<NamingSeries> existingDefaults = namingSeriesRepository.findBySeriesTypeAndIsDefaultTrueAndIsActiveTrue(seriesType)
                .stream().collect(Collectors.toList());
        
        for (NamingSeries series : existingDefaults) {
            series.setIsDefault(false);
            namingSeriesRepository.save(series);
        }
    }
    
    // Convert entity to DTO
    private NamingSeriesDto convertToDto(NamingSeries namingSeries) {
        return NamingSeriesDto.builder()
                .id(namingSeries.getId())
                .name(namingSeries.getName())
                .description(namingSeries.getDescription())
                .prefix(namingSeries.getPrefix())
                .suffix(namingSeries.getSuffix())
                .startNumber(namingSeries.getStartNumber())
                .currentNumber(namingSeries.getCurrentNumber())
                .padding(namingSeries.getPadding())
                .separator(namingSeries.getSeparator())
                .isActive(namingSeries.getIsActive())
                .isDefault(namingSeries.getIsDefault())
                .seriesType(namingSeries.getSeriesType())
                .notes(namingSeries.getNotes())
                .createdAt(namingSeries.getCreatedAt())
                .updatedAt(namingSeries.getUpdatedAt())
                .nextId(namingSeries.getNextId())
                .exampleId(namingSeries.getNextId())
                .build();
    }
}
