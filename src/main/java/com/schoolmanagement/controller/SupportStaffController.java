package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.SupportStaffDto;
import com.schoolmanagement.entity.SupportStaff;
import com.schoolmanagement.service.SupportStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support-staff")
@RequiredArgsConstructor
@Slf4j
public class SupportStaffController {
    
    private final SupportStaffService supportStaffService;
    
    // Create support staff
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<SupportStaffDto>> createSupportStaff(@Valid @RequestBody SupportStaffDto supportStaffDto) {
        log.info("Creating support staff: {}", supportStaffDto.getEmployeeId());
        return ResponseEntity.ok(supportStaffService.createSupportStaff(supportStaffDto));
    }
    
    // Get all support staff
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<ApiResponse<Page<SupportStaffDto>>> getAllSupportStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get all support staff request - page: {}, size: {}", page, size);
        return ResponseEntity.ok(supportStaffService.getAllSupportStaff(page, size));
    }
    
    // Get support staff by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<ApiResponse<SupportStaffDto>> getSupportStaffById(@PathVariable Long id) {
        log.info("Get support staff by ID: {}", id);
        return ResponseEntity.ok(supportStaffService.getSupportStaffById(id));
    }
    
    // Update support staff
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<SupportStaffDto>> updateSupportStaff(@PathVariable Long id, @Valid @RequestBody SupportStaffDto supportStaffDto) {
        log.info("Update support staff with ID: {}", id);
        return ResponseEntity.ok(supportStaffService.updateSupportStaff(id, supportStaffDto));
    }
    
    // Terminate support staff
    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<SupportStaffDto>> terminateSupportStaff(@PathVariable Long id, @RequestParam String reason) {
        log.info("Terminate support staff with ID: {}", id);
        return ResponseEntity.ok(supportStaffService.terminateSupportStaff(id, reason));
    }
    
    // Reactivate support staff
    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<SupportStaffDto>> reactivateSupportStaff(@PathVariable Long id) {
        log.info("Reactivate support staff with ID: {}", id);
        return ResponseEntity.ok(supportStaffService.reactivateSupportStaff(id));
    }
    
    // Delete support staff
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<Void>> deleteSupportStaff(@PathVariable Long id) {
        log.info("Delete support staff with ID: {}", id);
        return ResponseEntity.ok(supportStaffService.deleteSupportStaff(id));
    }
}
