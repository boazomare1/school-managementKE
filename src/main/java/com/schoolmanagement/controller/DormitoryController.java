package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.DormitoryDto;
import com.schoolmanagement.entity.Dormitory;
import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.repository.DormitoryRepository;
import com.schoolmanagement.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dormitories")
@RequiredArgsConstructor
@Slf4j
public class DormitoryController {
    
    private final DormitoryRepository dormitoryRepository;
    private final TeacherRepository teacherRepository;
    
    // Create dormitory
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<DormitoryDto>> createDormitory(@Valid @RequestBody DormitoryDto dormitoryDto) {
        try {
            log.info("Creating dormitory: {}", dormitoryDto.getName());
            
            // Check if dormitory already exists
            Optional<Dormitory> existingDormitory = dormitoryRepository.findByName(dormitoryDto.getName());
            if (existingDormitory.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Dormitory already exists"));
            }
            
            // Get dorm master if provided
            Teacher dormMaster = null;
            if (dormitoryDto.getDormMasterId() != null) {
                dormMaster = teacherRepository.findById(dormitoryDto.getDormMasterId()).orElse(null);
            }
            
            // Create dormitory entity
            Dormitory dormitory = new Dormitory();
            dormitory.setName(dormitoryDto.getName());
            dormitory.setDescription(dormitoryDto.getDescription());
            dormitory.setLocation(dormitoryDto.getLocation());
            dormitory.setCapacity(dormitoryDto.getCapacity());
            dormitory.setTotalRooms(dormitoryDto.getTotalRooms() != null ? dormitoryDto.getTotalRooms() : dormitoryDto.getCapacity());
            dormitory.setCurrentOccupancy(dormitoryDto.getCurrentOccupancy() != null ? dormitoryDto.getCurrentOccupancy() : 0);
            dormitory.setAvailableRooms(dormitoryDto.getAvailableRooms() != null ? dormitoryDto.getAvailableRooms() : dormitoryDto.getCapacity());
            dormitory.setOccupiedRooms(dormitoryDto.getOccupiedRooms() != null ? dormitoryDto.getOccupiedRooms() : 0);
            dormitory.setMonthlyFee(dormitoryDto.getMonthlyFee() != null ? dormitoryDto.getMonthlyFee() : BigDecimal.ZERO);
            dormitory.setDormMaster(dormMaster);
            dormitory.setRules(dormitoryDto.getRules());
            dormitory.setNotes(dormitoryDto.getNotes());
            dormitory.setIsActive(true);
            dormitory.setStatus(dormitoryDto.getStatus() != null ? Dormitory.DormitoryStatus.valueOf(dormitoryDto.getStatus()) : Dormitory.DormitoryStatus.ACTIVE);
            dormitory.setType(dormitoryDto.getType() != null ? Dormitory.DormitoryType.valueOf(dormitoryDto.getType()) : Dormitory.DormitoryType.MALE);
            
            Dormitory savedDormitory = dormitoryRepository.save(dormitory);
            DormitoryDto responseDto = convertToDto(savedDormitory);
            
            log.info("Successfully created dormitory with ID: {}", savedDormitory.getId());
            return ResponseEntity.ok(ApiResponse.success("Dormitory created successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error creating dormitory: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create dormitory: " + e.getMessage()));
        }
    }
    
    // Get all dormitories
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<DormitoryDto>>> getAllDormitories() {
        try {
            log.info("Fetching all dormitories");
            
            List<Dormitory> dormitories = dormitoryRepository.findByIsActiveTrue();
            List<DormitoryDto> dormitoryDtos = dormitories.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Dormitories retrieved successfully", dormitoryDtos));
            
        } catch (Exception e) {
            log.error("Error fetching dormitories: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch dormitories: " + e.getMessage()));
        }
    }
    
    // Get dormitory by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<DormitoryDto>> getDormitoryById(@PathVariable Long id) {
        try {
            log.info("Fetching dormitory by ID: {}", id);
            
            Optional<Dormitory> dormitoryOptional = dormitoryRepository.findById(id);
            if (dormitoryOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Dormitory not found"));
            }
            
            DormitoryDto responseDto = convertToDto(dormitoryOptional.get());
            return ResponseEntity.ok(ApiResponse.success("Dormitory retrieved successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error fetching dormitory: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch dormitory: " + e.getMessage()));
        }
    }
    
    // Assign dorm master
    @PutMapping("/{id}/assign-master/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<DormitoryDto>> assignDormMaster(@PathVariable Long id, @PathVariable Long teacherId) {
        try {
            log.info("Assigning teacher {} as dorm master for dormitory {}", teacherId, id);
            
            Optional<Dormitory> dormitoryOptional = dormitoryRepository.findById(id);
            if (dormitoryOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Dormitory not found"));
            }
            
            Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);
            if (teacherOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Teacher not found"));
            }
            
            Dormitory dormitory = dormitoryOptional.get();
            Teacher teacher = teacherOptional.get();
            
            dormitory.setDormMaster(teacher);
            
            Dormitory updatedDormitory = dormitoryRepository.save(dormitory);
            DormitoryDto responseDto = convertToDto(updatedDormitory);
            
            log.info("Successfully assigned teacher {} as dorm master for dormitory {}", teacherId, id);
            return ResponseEntity.ok(ApiResponse.success("Dorm master assigned successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error assigning dorm master: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to assign dorm master: " + e.getMessage()));
        }
    }
    
    // Convert entity to DTO
    private DormitoryDto convertToDto(Dormitory dormitory) {
        DormitoryDto dto = new DormitoryDto();
        dto.setId(dormitory.getId());
        dto.setName(dormitory.getName());
        dto.setDescription(dormitory.getDescription());
        dto.setLocation(dormitory.getLocation());
        dto.setCapacity(dormitory.getCapacity());
        dto.setTotalRooms(dormitory.getTotalRooms());
        dto.setCurrentOccupancy(dormitory.getCurrentOccupancy());
        dto.setAvailableRooms(dormitory.getAvailableRooms());
        dto.setOccupiedRooms(dormitory.getOccupiedRooms());
        dto.setMonthlyFee(dormitory.getMonthlyFee());
        dto.setIsActive(dormitory.getIsActive());
        dto.setStatus(dormitory.getStatus() != null ? dormitory.getStatus().toString() : "ACTIVE");
        dto.setType(dormitory.getType() != null ? dormitory.getType().toString() : "MALE");
        dto.setRules(dormitory.getRules());
        dto.setNotes(dormitory.getNotes());
        dto.setDormMasterId(dormitory.getDormMaster() != null ? dormitory.getDormMaster().getId() : null);
        dto.setDormMasterName(dormitory.getDormMaster() != null ? 
                dormitory.getDormMaster().getUser().getFirstName() + " " + 
                dormitory.getDormMaster().getUser().getLastName() : null);
        dto.setCreatedAt(dormitory.getCreatedAt());
        dto.setUpdatedAt(dormitory.getUpdatedAt());
        return dto;
    }
}
