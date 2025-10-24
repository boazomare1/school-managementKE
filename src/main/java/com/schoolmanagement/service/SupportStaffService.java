package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.SupportStaffDto;
import com.schoolmanagement.dto.NotificationRequestDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupportStaffService {

    private final SupportStaffRepository supportStaffRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // Create support staff
    public ApiResponse<SupportStaffDto> createSupportStaff(SupportStaffDto supportStaffDto) {
        try {
            log.info("Creating support staff: {}", supportStaffDto.getEmployeeId());

            // Check if user exists
            User user = userRepository.findById(supportStaffDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if employee ID already exists
            if (supportStaffRepository.findByEmployeeId(supportStaffDto.getEmployeeId()).isPresent()) {
                return ApiResponse.error("Employee ID already exists");
            }

            // Check if user already has a support staff record
            if (supportStaffRepository.findByUser(user).isPresent()) {
                return ApiResponse.error("User already has a support staff record");
            }

            // Create support staff
            SupportStaff supportStaff = SupportStaff.builder()
                    .user(user)
                    .employeeId(supportStaffDto.getEmployeeId())
                    .staffType(supportStaffDto.getStaffType())
                    .employmentStatus(supportStaffDto.getEmploymentStatus())
                    .hireDate(supportStaffDto.getHireDate())
                    .terminationDate(supportStaffDto.getTerminationDate())
                    .terminationReason(supportStaffDto.getTerminationReason())
                    .basicSalary(supportStaffDto.getBasicSalary())
                    .allowances(supportStaffDto.getAllowances())
                    .deductions(supportStaffDto.getDeductions())
                    .netSalary(supportStaffDto.getNetSalary())
                    .department(supportStaffDto.getDepartment())
                    .position(supportStaffDto.getPosition())
                    .supervisor(supportStaffDto.getSupervisor())
                    .workSchedule(supportStaffDto.getWorkSchedule())
                    .employmentType(supportStaffDto.getEmploymentType())
                    .bankName(supportStaffDto.getBankName())
                    .bankAccount(supportStaffDto.getBankAccount())
                    .nationalId(supportStaffDto.getNationalId())
                    .socialSecurityNumber(supportStaffDto.getSocialSecurityNumber())
                    .taxPin(supportStaffDto.getTaxPin())
                    .isActive(true)
                    .notes(supportStaffDto.getNotes())
                    .build();

            SupportStaff savedStaff = supportStaffRepository.save(supportStaff);
            log.info("Support staff created successfully: {}", savedStaff.getId());

            // Send onboarding notification - temporarily disabled
            // sendStaffOnboardingNotification(savedStaff);

            return ApiResponse.success("Support staff created successfully", convertToDto(savedStaff));

        } catch (Exception e) {
            log.error("Error creating support staff: {}", e.getMessage());
            return ApiResponse.error("Error creating support staff: " + e.getMessage());
        }
    }

    // Get all support staff with pagination
    public ApiResponse<Page<SupportStaffDto>> getAllSupportStaff(int page, int size) {
        try {
            log.info("Fetching all support staff - page: {}, size: {}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<SupportStaff> supportStaffPage = supportStaffRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
            Page<SupportStaffDto> dtoPage = supportStaffPage.map(this::convertToDto);
            return ApiResponse.success("Support staff retrieved successfully", dtoPage);
        } catch (Exception e) {
            log.error("Error fetching all support staff: {}", e.getMessage());
            return ApiResponse.error("Error fetching all support staff: " + e.getMessage());
        }
    }

    // Get support staff by ID
    public ApiResponse<SupportStaffDto> getSupportStaffById(Long id) {
        try {
            log.info("Fetching support staff by ID: {}", id);
            SupportStaff supportStaff = supportStaffRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Support staff not found"));
            return ApiResponse.success("Support staff retrieved successfully", convertToDto(supportStaff));
        } catch (Exception e) {
            log.error("Error fetching support staff by ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Error fetching support staff: " + e.getMessage());
        }
    }

    // Update support staff
    public ApiResponse<SupportStaffDto> updateSupportStaff(Long id, SupportStaffDto supportStaffDto) {
        try {
            log.info("Updating support staff with ID: {}", id);
            SupportStaff existingStaff = supportStaffRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Support staff not found"));

            // Update fields from DTO
            existingStaff.setEmployeeId(supportStaffDto.getEmployeeId());
            existingStaff.setStaffType(supportStaffDto.getStaffType());
            existingStaff.setEmploymentStatus(supportStaffDto.getEmploymentStatus());
            existingStaff.setHireDate(supportStaffDto.getHireDate());
            existingStaff.setTerminationDate(supportStaffDto.getTerminationDate());
            existingStaff.setTerminationReason(supportStaffDto.getTerminationReason());
            existingStaff.setBasicSalary(supportStaffDto.getBasicSalary());
            existingStaff.setAllowances(supportStaffDto.getAllowances());
            existingStaff.setDeductions(supportStaffDto.getDeductions());
            existingStaff.setNetSalary(supportStaffDto.getNetSalary());
            existingStaff.setDepartment(supportStaffDto.getDepartment());
            existingStaff.setPosition(supportStaffDto.getPosition());
            existingStaff.setSupervisor(supportStaffDto.getSupervisor());
            existingStaff.setWorkSchedule(supportStaffDto.getWorkSchedule());
            existingStaff.setEmploymentType(supportStaffDto.getEmploymentType());
            existingStaff.setBankName(supportStaffDto.getBankName());
            existingStaff.setBankAccount(supportStaffDto.getBankAccount());
            existingStaff.setNationalId(supportStaffDto.getNationalId());
            existingStaff.setSocialSecurityNumber(supportStaffDto.getSocialSecurityNumber());
            existingStaff.setTaxPin(supportStaffDto.getTaxPin());
            existingStaff.setIsActive(supportStaffDto.getIsActive());
            existingStaff.setNotes(supportStaffDto.getNotes());

            SupportStaff updatedStaff = supportStaffRepository.save(existingStaff);
            log.info("Support staff updated successfully: {}", updatedStaff.getId());
            return ApiResponse.success("Support staff updated successfully", convertToDto(updatedStaff));

        } catch (Exception e) {
            log.error("Error updating support staff with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Error updating support staff: " + e.getMessage());
        }
    }

    // Terminate support staff
    public ApiResponse<SupportStaffDto> terminateSupportStaff(Long id, String reason) {
        try {
            log.info("Terminating support staff with ID: {}", id);
            SupportStaff supportStaff = supportStaffRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Support staff not found"));

            supportStaff.setEmploymentStatus(SupportStaff.EmploymentStatus.TERMINATED);
            supportStaff.setTerminationDate(LocalDate.now());
            supportStaff.setTerminationReason(reason);
            supportStaff.setIsActive(false);

            SupportStaff updatedStaff = supportStaffRepository.save(supportStaff);
            log.info("Support staff terminated successfully: {}", updatedStaff.getId());

            // Send termination notification
            sendStaffTerminationNotification(updatedStaff, reason);

            return ApiResponse.success("Support staff terminated successfully", convertToDto(updatedStaff));

        } catch (Exception e) {
            log.error("Error terminating support staff with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Error terminating support staff: " + e.getMessage());
        }
    }

    // Reactivate support staff
    public ApiResponse<SupportStaffDto> reactivateSupportStaff(Long id) {
        try {
            log.info("Reactivating support staff with ID: {}", id);
            SupportStaff supportStaff = supportStaffRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Support staff not found"));

            supportStaff.setEmploymentStatus(SupportStaff.EmploymentStatus.ACTIVE);
            supportStaff.setTerminationDate(null);
            supportStaff.setTerminationReason(null);
            supportStaff.setIsActive(true);

            SupportStaff updatedStaff = supportStaffRepository.save(supportStaff);
            log.info("Support staff reactivated successfully: {}", updatedStaff.getId());
            return ApiResponse.success("Support staff reactivated successfully", convertToDto(updatedStaff));

        } catch (Exception e) {
            log.error("Error reactivating support staff with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Error reactivating support staff: " + e.getMessage());
        }
    }

    // Delete support staff (soft delete)
    public ApiResponse<Void> deleteSupportStaff(Long id) {
        try {
            log.info("Soft deleting support staff with ID: {}", id);
            SupportStaff supportStaff = supportStaffRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Support staff not found"));

            supportStaff.setIsActive(false);
            supportStaffRepository.save(supportStaff);
            log.info("Support staff soft deleted successfully: {}", id);
            return ApiResponse.success("Support staff soft deleted successfully", null);

        } catch (Exception e) {
            log.error("Error soft deleting support staff with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Error soft deleting support staff: " + e.getMessage());
        }
    }

    // Helper method to convert entity to DTO
    private SupportStaffDto convertToDto(SupportStaff supportStaff) {
        return SupportStaffDto.builder()
                .id(supportStaff.getId())
                .userId(supportStaff.getUser().getId())
                .employeeId(supportStaff.getEmployeeId())
                .staffType(supportStaff.getStaffType())
                .employmentStatus(supportStaff.getEmploymentStatus())
                .hireDate(supportStaff.getHireDate())
                .terminationDate(supportStaff.getTerminationDate())
                .terminationReason(supportStaff.getTerminationReason())
                .basicSalary(supportStaff.getBasicSalary())
                .allowances(supportStaff.getAllowances())
                .deductions(supportStaff.getDeductions())
                .netSalary(supportStaff.getNetSalary())
                .department(supportStaff.getDepartment())
                .position(supportStaff.getPosition())
                .supervisor(supportStaff.getSupervisor())
                .workSchedule(supportStaff.getWorkSchedule())
                .employmentType(supportStaff.getEmploymentType())
                .bankName(supportStaff.getBankName())
                .bankAccount(supportStaff.getBankAccount())
                .nationalId(supportStaff.getNationalId())
                .socialSecurityNumber(supportStaff.getSocialSecurityNumber())
                .taxPin(supportStaff.getTaxPin())
                .isActive(supportStaff.getIsActive())
                .notes(supportStaff.getNotes())
                .createdAt(supportStaff.getCreatedAt())
                .updatedAt(supportStaff.getUpdatedAt())
                // User details for response
                .username(supportStaff.getUser().getUsername())
                .email(supportStaff.getUser().getEmail())
                .firstName(supportStaff.getUser().getFirstName())
                .lastName(supportStaff.getUser().getLastName())
                .phoneNumber(supportStaff.getUser().getPhoneNumber())
                .address(supportStaff.getUser().getAddress())
                .build();
    }

    // Send onboarding notification to new staff
    private void sendStaffOnboardingNotification(SupportStaff supportStaff) {
        try {
            String welcomeMessage = String.format(
                    "Welcome, %s %s! Your %s account has been successfully created and you are now onboarded as a %s. " +
                            "Your employee ID is %s. Please review your details and contact HR if you have any questions.",
                    supportStaff.getUser().getFirstName(),
                    supportStaff.getUser().getLastName(),
                    supportStaff.getStaffType().name().toLowerCase(),
                    supportStaff.getPosition(),
                    supportStaff.getEmployeeId()
            );

            NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                    .title("Welcome to the Support Staff Team!")
                    .message(welcomeMessage)
                    .type(Notification.NotificationType.STAFF_ONBOARDING)
                    .priority(Notification.NotificationPriority.HIGH)
                    .recipientId(supportStaff.getUser().getId())
                    .actionUrl("/support-staff/dashboard")
                    .actionText("View Staff Dashboard")
                    .build();

            notificationService.createNotification(notificationRequest);
            log.info("Staff onboarding notification sent to user: {}", supportStaff.getUser().getUsername());

        } catch (Exception e) {
            log.error("Error sending onboarding notification to staff {}: {}", supportStaff.getUser().getUsername(), e.getMessage());
        }
    }

    // Send termination notification to staff
    private void sendStaffTerminationNotification(SupportStaff supportStaff, String reason) {
        try {
            String terminationMessage = String.format(
                    "Dear %s %s, this is to inform you that your employment as a %s (%s) has been terminated. " +
                            "Reason for termination: %s. Please contact HR for further details regarding your exit process.",
                    supportStaff.getUser().getFirstName(),
                    supportStaff.getUser().getLastName(),
                    supportStaff.getPosition(),
                    supportStaff.getEmployeeId(),
                    reason
            );

            NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                    .title("Employment Termination Notice")
                    .message(terminationMessage)
                    .type(Notification.NotificationType.STAFF_TERMINATION)
                    .priority(Notification.NotificationPriority.HIGH)
                    .recipientId(supportStaff.getUser().getId())
                    .actionUrl("/contact-hr")
                    .actionText("Contact HR")
                    .build();

            notificationService.createNotification(notificationRequest);
            log.info("Staff termination notification sent to user: {}", supportStaff.getUser().getUsername());

        } catch (Exception e) {
            log.error("Error sending termination notification to staff {}: {}", supportStaff.getUser().getUsername(), e.getMessage());
        }
    }
}
