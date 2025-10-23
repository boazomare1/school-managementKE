package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.SchoolDto;
import com.schoolmanagement.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
@Slf4j
public class SchoolController {
    
    private final SchoolService schoolService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SchoolDto>> createSchool(@Valid @RequestBody SchoolDto schoolDto) {
        log.info("Creating school request: {}", schoolDto.getName());
        return ResponseEntity.ok(schoolService.createSchool(schoolDto));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<SchoolDto>>> getAllSchools() {
        log.info("Get all schools request");
        return ResponseEntity.ok(schoolService.getAllSchools());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<SchoolDto>> getSchoolById(@PathVariable Long id) {
        log.info("Get school by ID request: {}", id);
        return ResponseEntity.ok(schoolService.getSchoolById(id));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SchoolDto>> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolDto schoolDto) {
        log.info("Update school request: {}", id);
        return ResponseEntity.ok(schoolService.updateSchool(id, schoolDto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable Long id) {
        log.info("Delete school request: {}", id);
        return ResponseEntity.ok(schoolService.deleteSchool(id));
    }
}


