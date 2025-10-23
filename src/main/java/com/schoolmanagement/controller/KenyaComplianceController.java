package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.CapitationGrantDto;
import com.schoolmanagement.dto.CbcCompetencyDto;
import com.schoolmanagement.dto.KenyaFeeStructureDto;
import com.schoolmanagement.dto.NemisStudentDto;
import com.schoolmanagement.dto.StudentCompetencyDto;
import com.schoolmanagement.service.KenyaComplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kenya")
@RequiredArgsConstructor
@Slf4j
public class KenyaComplianceController {

    private final KenyaComplianceService kenyaComplianceService;

    // NEMIS Student Management
    @PostMapping("/nemis/register")
    public ResponseEntity<ApiResponse<NemisStudentDto>> registerNemisStudent(@RequestBody NemisStudentDto request) {
        log.info("Registering NEMIS student: {}", request.getNemisNumber());
        ApiResponse<NemisStudentDto> response = kenyaComplianceService.registerNemisStudent(request);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @GetMapping("/nemis/school/{schoolId}")
    public ResponseEntity<ApiResponse<List<NemisStudentDto>>> getNemisStudentsBySchool(@PathVariable Long schoolId) {
        log.info("Fetching NEMIS students for school: {}", schoolId);
        ApiResponse<List<NemisStudentDto>> response = kenyaComplianceService.getNemisStudentsBySchool(schoolId);
        return ResponseEntity.ok(response);
    }

    // CBC Competency Management
    @GetMapping("/cbc/competencies/grade/{gradeLevel}")
    public ResponseEntity<ApiResponse<List<CbcCompetencyDto>>> getCbcCompetenciesByGrade(@PathVariable String gradeLevel) {
        log.info("Fetching CBC competencies for grade: {}", gradeLevel);
        ApiResponse<List<CbcCompetencyDto>> response = kenyaComplianceService.getCbcCompetenciesByGrade(gradeLevel);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cbc/assess")
    public ResponseEntity<ApiResponse<StudentCompetencyDto>> assessStudentCompetency(@RequestBody StudentCompetencyDto request) {
        log.info("Assessing student competency: {} for student: {}", request.getCompetencyId(), request.getStudentId());
        ApiResponse<StudentCompetencyDto> response = kenyaComplianceService.assessStudentCompetency(request);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @GetMapping("/cbc/student/{studentId}/competencies")
    public ResponseEntity<ApiResponse<List<StudentCompetencyDto>>> getStudentCompetencies(@PathVariable Long studentId) {
        log.info("Fetching competencies for student: {}", studentId);
        ApiResponse<List<StudentCompetencyDto>> response = kenyaComplianceService.getStudentCompetencies(studentId);
        return ResponseEntity.ok(response);
    }

    // Kenya Fee Structure Management
    @PostMapping("/fees/structure")
    public ResponseEntity<ApiResponse<KenyaFeeStructureDto>> createKenyaFeeStructure(@RequestBody KenyaFeeStructureDto request) {
        log.info("Creating Kenya fee structure: {} for school: {}", request.getFeeName(), request.getSchoolId());
        ApiResponse<KenyaFeeStructureDto> response = kenyaComplianceService.createKenyaFeeStructure(request);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @GetMapping("/fees/school/{schoolId}")
    public ResponseEntity<ApiResponse<List<KenyaFeeStructureDto>>> getKenyaFeeStructuresBySchool(@PathVariable Long schoolId) {
        log.info("Fetching Kenya fee structures for school: {}", schoolId);
        ApiResponse<List<KenyaFeeStructureDto>> response = kenyaComplianceService.getKenyaFeeStructuresBySchool(schoolId);
        return ResponseEntity.ok(response);
    }

    // Capitation Grant Management
    @PostMapping("/grants/capitation")
    public ResponseEntity<ApiResponse<CapitationGrantDto>> createCapitationGrant(@RequestBody CapitationGrantDto request) {
        log.info("Creating capitation grant: {} for school: {}", request.getGrantNumber(), request.getSchoolId());
        ApiResponse<CapitationGrantDto> response = kenyaComplianceService.createCapitationGrant(request);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @GetMapping("/grants/school/{schoolId}")
    public ResponseEntity<ApiResponse<List<CapitationGrantDto>>> getCapitationGrantsBySchool(@PathVariable Long schoolId) {
        log.info("Fetching capitation grants for school: {}", schoolId);
        ApiResponse<List<CapitationGrantDto>> response = kenyaComplianceService.getCapitationGrantsBySchool(schoolId);
        return ResponseEntity.ok(response);
    }

    // Kenya Dashboard
    @GetMapping("/dashboard/school/{schoolId}")
    public ResponseEntity<ApiResponse<Object>> getKenyaDashboard(@PathVariable Long schoolId) {
        log.info("Fetching Kenya dashboard for school: {}", schoolId);
        
        // Get NEMIS students
        ApiResponse<List<NemisStudentDto>> nemisResponse = kenyaComplianceService.getNemisStudentsBySchool(schoolId);
        
        // Get fee structures
        ApiResponse<List<KenyaFeeStructureDto>> feesResponse = kenyaComplianceService.getKenyaFeeStructuresBySchool(schoolId);
        
        // Get capitation grants
        ApiResponse<List<CapitationGrantDto>> grantsResponse = kenyaComplianceService.getCapitationGrantsBySchool(schoolId);
        
        // Create dashboard object
        Object dashboard = new Object() {
            public final List<NemisStudentDto> nemisStudents = nemisResponse.getData();
            public final List<KenyaFeeStructureDto> feeStructures = feesResponse.getData();
            public final List<CapitationGrantDto> capitationGrants = grantsResponse.getData();
            public final int totalNemisStudents = nemisResponse.getData() != null ? nemisResponse.getData().size() : 0;
            public final int totalFeeStructures = feesResponse.getData() != null ? feesResponse.getData().size() : 0;
            public final int totalCapitationGrants = grantsResponse.getData() != null ? grantsResponse.getData().size() : 0;
        };
        
        ApiResponse<Object> response = ApiResponse.success("Kenya dashboard retrieved successfully", dashboard);
        return ResponseEntity.ok(response);
    }
}

