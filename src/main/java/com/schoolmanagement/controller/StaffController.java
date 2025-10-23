package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.StaffDto;
import com.schoolmanagement.entity.Staff;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.StaffRepository;
import com.schoolmanagement.repository.UserRepository;
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

class StaffStatistics {
    public long total;
    public long teachers;
    public long admins;
    public long support;
}

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Slf4j
public class StaffController {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<StaffDto>>> getAllStaff() {
        try {
            log.info("Fetching all staff members");
            List<Staff> staffList = staffRepository.findByIsActiveTrueOrderByUserFirstNameAsc();
            List<StaffDto> staffDtos = staffList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", staffDtos));
        } catch (Exception e) {
            log.error("Error fetching staff: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch staff: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<StaffDto>> getStaffById(@PathVariable Long id) {
        try {
            log.info("Fetching staff member with ID: {}", id);
            Optional<Staff> staffOptional = staffRepository.findById(id);
            if (staffOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            StaffDto staffDto = convertToDto(staffOptional.get());
            return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", staffDto));
        } catch (Exception e) {
            log.error("Error fetching staff by ID: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch staff: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{staffType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<StaffDto>>> getStaffByType(@PathVariable Staff.StaffType staffType) {
        try {
            log.info("Fetching staff by type: {}", staffType);
            List<Staff> staffList = staffRepository.findByStaffTypeAndIsActiveTrueOrderByUserFirstNameAsc(staffType);
            List<StaffDto> staffDtos = staffList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", staffDtos));
        } catch (Exception e) {
            log.error("Error fetching staff by type: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch staff: " + e.getMessage()));
        }
    }

    @GetMapping("/employment/{employmentType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<StaffDto>>> getStaffByEmploymentType(@PathVariable Staff.EmploymentType employmentType) {
        try {
            log.info("Fetching staff by employment type: {}", employmentType);
            List<Staff> staffList = staffRepository.findByEmploymentTypeAndIsActiveTrueOrderByUserFirstNameAsc(employmentType);
            List<StaffDto> staffDtos = staffList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", staffDtos));
        } catch (Exception e) {
            log.error("Error fetching staff by employment type: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch staff: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<StaffDto>> createStaff(@RequestBody StaffDto staffDto, Authentication authentication) {
        try {
            log.info("Creating new staff member: {}", staffDto.getEmployeeNumber());

            // Find the user
            Optional<User> userOptional = userRepository.findById(staffDto.getUserId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
            }

            // Check if staff already exists for this user
            Optional<Staff> existingStaff = staffRepository.findByUserIdAndIsActiveTrue(staffDto.getUserId());
            if (existingStaff.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Staff record already exists for this user"));
            }

            // Create staff record
            Staff staff = Staff.builder()
                    .user(userOptional.get())
                    .employeeNumber(staffDto.getEmployeeNumber())
                    .tscNumber(staffDto.getTscNumber())
                    .staffType(staffDto.getStaffType())
                    .employmentType(staffDto.getEmploymentType())
                    .employmentDate(staffDto.getEmploymentDate())
                    .terminationDate(staffDto.getTerminationDate())
                    .basicSalary(staffDto.getBasicSalary())
                    .houseAllowance(staffDto.getHouseAllowance())
                    .transportAllowance(staffDto.getTransportAllowance())
                    .medicalAllowance(staffDto.getMedicalAllowance())
                    .otherAllowances(staffDto.getOtherAllowances())
                    .nhifDeduction(staffDto.getNhifDeduction())
                    .nssfDeduction(staffDto.getNssfDeduction())
                    .payeDeduction(staffDto.getPayeDeduction())
                    .otherDeductions(staffDto.getOtherDeductions())
                    .qualifications(staffDto.getQualifications())
                    .certifications(staffDto.getCertifications())
                    .experience(staffDto.getExperience())
                    .bankName(staffDto.getBankName())
                    .bankAccount(staffDto.getBankAccount())
                    .nhifNumber(staffDto.getNhifNumber())
                    .nssfNumber(staffDto.getNssfNumber())
                    .kraPin(staffDto.getKraPin())
                    .isActive(true)
                    .build();

            Staff savedStaff = staffRepository.save(staff);
            StaffDto responseDto = convertToDto(savedStaff);
            return ResponseEntity.ok(ApiResponse.success("Staff created successfully", responseDto));

        } catch (Exception e) {
            log.error("Error creating staff: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to create staff: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<StaffDto>> updateStaff(@PathVariable Long id, @RequestBody StaffDto staffDto, Authentication authentication) {
        try {
            log.info("Updating staff member with ID: {}", id);

            Optional<Staff> staffOptional = staffRepository.findById(id);
            if (staffOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Staff staff = staffOptional.get();
            staff.setEmployeeNumber(staffDto.getEmployeeNumber());
            staff.setTscNumber(staffDto.getTscNumber());
            staff.setStaffType(staffDto.getStaffType());
            staff.setEmploymentType(staffDto.getEmploymentType());
            staff.setEmploymentDate(staffDto.getEmploymentDate());
            staff.setTerminationDate(staffDto.getTerminationDate());
            staff.setBasicSalary(staffDto.getBasicSalary());
            staff.setHouseAllowance(staffDto.getHouseAllowance());
            staff.setTransportAllowance(staffDto.getTransportAllowance());
            staff.setMedicalAllowance(staffDto.getMedicalAllowance());
            staff.setOtherAllowances(staffDto.getOtherAllowances());
            staff.setNhifDeduction(staffDto.getNhifDeduction());
            staff.setNssfDeduction(staffDto.getNssfDeduction());
            staff.setPayeDeduction(staffDto.getPayeDeduction());
            staff.setOtherDeductions(staffDto.getOtherDeductions());
            staff.setQualifications(staffDto.getQualifications());
            staff.setCertifications(staffDto.getCertifications());
            staff.setExperience(staffDto.getExperience());
            staff.setBankName(staffDto.getBankName());
            staff.setBankAccount(staffDto.getBankAccount());
            staff.setNhifNumber(staffDto.getNhifNumber());
            staff.setNssfNumber(staffDto.getNssfNumber());
            staff.setKraPin(staffDto.getKraPin());

            Staff savedStaff = staffRepository.save(staff);
            StaffDto responseDto = convertToDto(savedStaff);
            return ResponseEntity.ok(ApiResponse.success("Staff updated successfully", responseDto));

        } catch (Exception e) {
            log.error("Error updating staff: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to update staff: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<String>> deleteStaff(@PathVariable Long id, Authentication authentication) {
        try {
            log.info("Deactivating staff member with ID: {}", id);

            Optional<Staff> staffOptional = staffRepository.findById(id);
            if (staffOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Staff staff = staffOptional.get();
            staff.setIsActive(false);
            staffRepository.save(staff);

            return ResponseEntity.ok(ApiResponse.success("Staff deactivated successfully", "Staff member deactivated"));

        } catch (Exception e) {
            log.error("Error deactivating staff: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to deactivate staff: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<Object>> getStaffStatistics() {
        try {
            log.info("Fetching staff statistics");

            List<Staff> allStaff = staffRepository.findByIsActiveTrueOrderByUserFirstNameAsc();
            
            long totalStaff = allStaff.size();
            long teachers = staffRepository.countByStaffTypeAndIsActiveTrue(Staff.StaffType.TEACHER);
            long admins = staffRepository.countByStaffTypeAndIsActiveTrue(Staff.StaffType.ADMIN);
            long support = staffRepository.countByStaffTypeAndIsActiveTrue(Staff.StaffType.SUPPORT);

            StaffStatistics stats = new StaffStatistics();
            stats.total = totalStaff;
            stats.teachers = teachers;
            stats.admins = admins;
            stats.support = support;
            
            return ResponseEntity.ok(ApiResponse.success("Staff statistics retrieved successfully", stats));

        } catch (Exception e) {
            log.error("Error fetching staff statistics: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to fetch staff statistics: " + e.getMessage()));
        }
    }

    private StaffDto convertToDto(Staff staff) {
        return StaffDto.builder()
                .id(staff.getId())
                .userId(staff.getUser().getId())
                .employeeNumber(staff.getEmployeeNumber())
                .tscNumber(staff.getTscNumber())
                .staffType(staff.getStaffType())
                .employmentType(staff.getEmploymentType())
                .employmentDate(staff.getEmploymentDate())
                .terminationDate(staff.getTerminationDate())
                .basicSalary(staff.getBasicSalary())
                .houseAllowance(staff.getHouseAllowance())
                .transportAllowance(staff.getTransportAllowance())
                .medicalAllowance(staff.getMedicalAllowance())
                .otherAllowances(staff.getOtherAllowances())
                .nhifDeduction(staff.getNhifDeduction())
                .nssfDeduction(staff.getNssfDeduction())
                .payeDeduction(staff.getPayeDeduction())
                .otherDeductions(staff.getOtherDeductions())
                .isActive(staff.getIsActive())
                .qualifications(staff.getQualifications())
                .certifications(staff.getCertifications())
                .experience(staff.getExperience())
                .bankName(staff.getBankName())
                .bankAccount(staff.getBankAccount())
                .nhifNumber(staff.getNhifNumber())
                .nssfNumber(staff.getNssfNumber())
                .kraPin(staff.getKraPin())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .firstName(staff.getUser().getFirstName())
                .lastName(staff.getUser().getLastName())
                .email(staff.getUser().getEmail())
                .phoneNumber(staff.getUser().getPhoneNumber())
                .address(staff.getUser().getAddress())
                .build();
    }
}
