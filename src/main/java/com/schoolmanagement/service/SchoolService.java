package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.SchoolDto;
import com.schoolmanagement.entity.School;
import com.schoolmanagement.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SchoolService {
    
    private final SchoolRepository schoolRepository;
    
    public ApiResponse<SchoolDto> createSchool(SchoolDto schoolDto) {
        try {
            log.info("Creating school: {}", schoolDto.getName());
            
            // Check if school with same name already exists
            Optional<School> existingSchool = schoolRepository.findByName(schoolDto.getName());
            if (existingSchool.isPresent()) {
                return ApiResponse.error("School with name '" + schoolDto.getName() + "' already exists");
            }
            
            School school = new School();
            school.setName(schoolDto.getName());
            school.setAddress(schoolDto.getAddress());
            school.setPhone(schoolDto.getPhone());
            school.setEmail(schoolDto.getEmail());
            school.setWebsite(schoolDto.getWebsite());
            school.setRegistrationNumber(schoolDto.getRegistrationNumber());
            school.setPrincipalName(schoolDto.getPrincipalName());
            school.setPrincipalPhone(schoolDto.getPrincipalPhone());
            school.setPrincipalEmail(schoolDto.getPrincipalEmail());
            school.setIsActive(schoolDto.getIsActive() != null ? schoolDto.getIsActive() : true);
            
            School savedSchool = schoolRepository.save(school);
            log.info("School created successfully: {}", savedSchool.getId());
            
            return ApiResponse.success("School created successfully", convertToDto(savedSchool));
            
        } catch (Exception e) {
            log.error("Error creating school: {}", e.getMessage());
            return ApiResponse.error("Failed to create school: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<List<SchoolDto>> getAllSchools() {
        try {
            log.info("Fetching all schools");
            List<School> schools = schoolRepository.findAllActiveSchools();
            List<SchoolDto> schoolDtos = schools.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Schools retrieved successfully", schoolDtos);
            
        } catch (Exception e) {
            log.error("Error fetching schools: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve schools: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public ApiResponse<SchoolDto> getSchoolById(Long id) {
        try {
            log.info("Fetching school by ID: {}", id);
            Optional<School> school = schoolRepository.findById(id);
            
            if (school.isEmpty()) {
                return ApiResponse.error("School not found");
            }
            
            return ApiResponse.success("School retrieved successfully", convertToDto(school.get()));
            
        } catch (Exception e) {
            log.error("Error fetching school by ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to retrieve school: " + e.getMessage());
        }
    }
    
    public ApiResponse<SchoolDto> updateSchool(Long id, SchoolDto schoolDto) {
        try {
            log.info("Updating school: {}", id);
            Optional<School> schoolOpt = schoolRepository.findById(id);
            
            if (schoolOpt.isEmpty()) {
                return ApiResponse.error("School not found");
            }
            
            School school = schoolOpt.get();
            school.setName(schoolDto.getName());
            school.setAddress(schoolDto.getAddress());
            school.setPhone(schoolDto.getPhone());
            school.setEmail(schoolDto.getEmail());
            school.setWebsite(schoolDto.getWebsite());
            school.setRegistrationNumber(schoolDto.getRegistrationNumber());
            school.setPrincipalName(schoolDto.getPrincipalName());
            school.setPrincipalPhone(schoolDto.getPrincipalPhone());
            school.setPrincipalEmail(schoolDto.getPrincipalEmail());
            school.setIsActive(schoolDto.getIsActive());
            
            School updatedSchool = schoolRepository.save(school);
            log.info("School updated successfully: {}", updatedSchool.getId());
            
            return ApiResponse.success("School updated successfully", convertToDto(updatedSchool));
            
        } catch (Exception e) {
            log.error("Error updating school {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to update school: " + e.getMessage());
        }
    }
    
    public ApiResponse<Void> deleteSchool(Long id) {
        try {
            log.info("Deleting school: {}", id);
            Optional<School> school = schoolRepository.findById(id);
            
            if (school.isEmpty()) {
                return ApiResponse.error("School not found");
            }
            
            school.get().setIsActive(false);
            schoolRepository.save(school.get());
            log.info("School deleted successfully: {}", id);
            
            return ApiResponse.success("School deleted successfully", null);
            
        } catch (Exception e) {
            log.error("Error deleting school {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to delete school: " + e.getMessage());
        }
    }
    
    private SchoolDto convertToDto(School school) {
        SchoolDto dto = new SchoolDto();
        dto.setId(school.getId());
        dto.setName(school.getName());
        dto.setAddress(school.getAddress());
        dto.setPhone(school.getPhone());
        dto.setEmail(school.getEmail());
        dto.setWebsite(school.getWebsite());
        dto.setRegistrationNumber(school.getRegistrationNumber());
        dto.setPrincipalName(school.getPrincipalName());
        dto.setPrincipalPhone(school.getPrincipalPhone());
        dto.setPrincipalEmail(school.getPrincipalEmail());
        dto.setIsActive(school.getIsActive());
        return dto;
    }
}
