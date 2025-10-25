package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.CapitationGrantDto;
import com.schoolmanagement.dto.CbcCompetencyDto;
import com.schoolmanagement.dto.KenyaFeeStructureDto;
import com.schoolmanagement.dto.NemisStudentDto;
import com.schoolmanagement.dto.StudentCompetencyDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KenyaComplianceService {

    private final NemisStudentRepository nemisStudentRepository;
    private final CbcCompetencyRepository cbcCompetencyRepository;
    private final StudentCompetencyRepository studentCompetencyRepository;
    private final KenyaFeeStructureRepository kenyaFeeStructureRepository;
    private final CapitationGrantRepository capitationGrantRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ClassRepository classRepository;
    private final TermRepository termRepository;

    // NEMIS Student Management
    @Transactional
    public ApiResponse<NemisStudentDto> registerNemisStudent(NemisStudentDto request) {
        try {
            log.info("Registering NEMIS student: {}", request.getNemisNumber());

            User student = userRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            NemisStudent nemisStudent = NemisStudent.builder()
                    .student(student)
                    .nemisNumber(request.getNemisNumber())
                    .upiNumber(request.getUpiNumber())
                    .birthCertificateNumber(request.getBirthCertificateNumber())
                    .nationalIdNumber(request.getNationalIdNumber())
                    .county(request.getCounty())
                    .subCounty(request.getSubCounty())
                    .ward(request.getWard())
                    .constituency(request.getConstituency())
                    .disabilityStatus(NemisStudent.DisabilityStatus.valueOf(request.getDisabilityStatus()))
                    .disabilityDescription(request.getDisabilityDescription())
                    .orphanStatus(NemisStudent.OrphanStatus.valueOf(request.getOrphanStatus()))
                    .vulnerableStatus(NemisStudent.VulnerableStatus.valueOf(request.getVulnerableStatus()))
                    .isSpecialNeeds(request.getIsSpecialNeeds())
                    .specialNeedsDescription(request.getSpecialNeedsDescription())
                    .isOrphan(request.getIsOrphan())
                    .isVulnerable(request.getIsVulnerable())
                    .isBursaryRecipient(request.getIsBursaryRecipient())
                    .bursarySource(request.getBursarySource())
                    .isCapitationRecipient(request.getIsCapitationRecipient())
                    .isNemisSynced(false)
                    .isActive(true)
                    .build();

            NemisStudent savedStudent = nemisStudentRepository.save(nemisStudent);
            return ApiResponse.success("NEMIS student registered successfully", convertToDto(savedStudent));

        } catch (Exception e) {
            log.error("Error registering NEMIS student: {}", e.getMessage());
            return ApiResponse.error("Failed to register NEMIS student: " + e.getMessage());
        }
    }

    // CBC Competency Management
    @Transactional
    public ApiResponse<StudentCompetencyDto> assessStudentCompetency(StudentCompetencyDto request) {
        try {
            log.info("Assessing student competency: {} for student: {}", request.getCompetencyId(), request.getStudentId());

            User student = userRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            CbcCompetency competency = cbcCompetencyRepository.findById(request.getCompetencyId())
                    .orElseThrow(() -> new RuntimeException("Competency not found"));
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new RuntimeException("Academic year not found"));
            Term term = termRepository.findById(request.getTermId())
                    .orElseThrow(() -> new RuntimeException("Term not found"));

            StudentCompetency studentCompetency = StudentCompetency.builder()
                    .student(student)
                    .competency(competency)
                    .teacher(teacher)
                    .academicYear(academicYear)
                    .term(term)
                    .level(StudentCompetency.CompetencyLevel.valueOf(request.getLevel()))
                    .assessmentType(StudentCompetency.AssessmentType.valueOf(request.getAssessmentType()))
                    .evidence(request.getEvidence())
                    .teacherComments(request.getTeacherComments())
                    .nextSteps(request.getNextSteps())
                    .isActive(true)
                    .build();

            StudentCompetency savedCompetency = studentCompetencyRepository.save(studentCompetency);
            return ApiResponse.success("Student competency assessed successfully", convertToDto(savedCompetency));

        } catch (Exception e) {
            log.error("Error assessing student competency: {}", e.getMessage());
            return ApiResponse.error("Failed to assess student competency: " + e.getMessage());
        }
    }

    // Kenya Fee Structure Management
    @Transactional
    public ApiResponse<KenyaFeeStructureDto> createKenyaFeeStructure(KenyaFeeStructureDto request) {
        try {
            log.info("Creating Kenya fee structure: {} for school: {}", request.getFeeName(), request.getSchoolId());

            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
            ClassEntity classEntity = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new RuntimeException("Academic year not found"));

            KenyaFeeStructure feeStructure = KenyaFeeStructure.builder()
                    .school(school)
                    .classEntity(classEntity)
                    .academicYear(academicYear)
                    .feeName(request.getFeeName())
                    .feeCode(request.getFeeCode())
                    .feeType(KenyaFeeStructure.FeeType.valueOf(request.getFeeType()))
                    .amount(request.getAmount())
                    .capitationAmount(request.getCapitationAmount())
                    .parentContribution(request.getParentContribution())
                    .frequency(KenyaFeeStructure.PaymentFrequency.valueOf(request.getFrequency()))
                    .isMandatory(request.getIsMandatory())
                    .isCapitationEligible(request.getIsCapitationEligible())
                    .isBursaryEligible(request.getIsBursaryEligible())
                    .isActive(true)
                    .build();

            KenyaFeeStructure savedFeeStructure = kenyaFeeStructureRepository.save(feeStructure);
            return ApiResponse.success("Kenya fee structure created successfully", convertToDto(savedFeeStructure));

        } catch (Exception e) {
            log.error("Error creating Kenya fee structure: {}", e.getMessage());
            return ApiResponse.error("Failed to create Kenya fee structure: " + e.getMessage());
        }
    }

    // Capitation Grant Management
    @Transactional
    public ApiResponse<CapitationGrantDto> createCapitationGrant(CapitationGrantDto request) {
        try {
            log.info("Creating capitation grant: {} for school: {}", request.getGrantNumber(), request.getSchoolId());

            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new RuntimeException("Academic year not found"));

            CapitationGrant capitationGrant = CapitationGrant.builder()
                    .school(school)
                    .academicYear(academicYear)
                    .grantNumber(request.getGrantNumber())
                    .totalAmount(request.getTotalAmount())
                    .receivedAmount(request.getReceivedAmount())
                    .pendingAmount(request.getPendingAmount())
                    .status(CapitationGrant.GrantStatus.valueOf(request.getStatus()))
                    .expectedDate(request.getExpectedDate())
                    .receivedDate(request.getReceivedDate() != null ? request.getReceivedDate() : null)
                    .studentCount(request.getStudentCount())
                    .perStudentAmount(request.getPerStudentAmount())
                    .remarks(request.getRemarks())
                    .isActive(true)
                    .build();

            CapitationGrant savedGrant = capitationGrantRepository.save(capitationGrant);
            return ApiResponse.success("Capitation grant created successfully", convertToDto(savedGrant));

        } catch (Exception e) {
            log.error("Error creating capitation grant: {}", e.getMessage());
            return ApiResponse.error("Failed to create capitation grant: " + e.getMessage());
        }
    }

    // Get NEMIS students by school
    public ApiResponse<List<NemisStudentDto>> getNemisStudentsBySchool(Long schoolId) {
        try {
            log.info("Fetching NEMIS students for school: {}", schoolId);
            List<NemisStudent> students = nemisStudentRepository.findBySchoolId(schoolId);
            List<NemisStudentDto> studentDtos = students.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("NEMIS students retrieved successfully", studentDtos);
        } catch (Exception e) {
            log.error("Error fetching NEMIS students: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve NEMIS students: " + e.getMessage());
        }
    }

    // Get CBC competencies by grade level
    public ApiResponse<List<CbcCompetencyDto>> getCbcCompetenciesByGrade(String gradeLevel) {
        try {
            log.info("Fetching CBC competencies for grade: {}", gradeLevel);
            List<CbcCompetency> competencies = cbcCompetencyRepository.findByGradeLevelAndIsActiveTrue(
                    CbcCompetency.GradeLevel.valueOf(gradeLevel));
            List<CbcCompetencyDto> competencyDtos = competencies.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("CBC competencies retrieved successfully", competencyDtos);
        } catch (Exception e) {
            log.error("Error fetching CBC competencies: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve CBC competencies: " + e.getMessage());
        }
    }

    // Get student competencies
    public ApiResponse<List<StudentCompetencyDto>> getStudentCompetencies(Long studentId) {
        try {
            log.info("Fetching competencies for student: {}", studentId);
            List<StudentCompetency> competencies = studentCompetencyRepository.findByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId);
            List<StudentCompetencyDto> competencyDtos = competencies.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("Student competencies retrieved successfully", competencyDtos);
        } catch (Exception e) {
            log.error("Error fetching student competencies: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve student competencies: " + e.getMessage());
        }
    }

    // Get Kenya fee structures by school
    public ApiResponse<List<KenyaFeeStructureDto>> getKenyaFeeStructuresBySchool(Long schoolId) {
        try {
            log.info("Fetching Kenya fee structures for school: {}", schoolId);
            List<KenyaFeeStructure> feeStructures = kenyaFeeStructureRepository.findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(schoolId);
            List<KenyaFeeStructureDto> feeStructureDtos = feeStructures.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("Kenya fee structures retrieved successfully", feeStructureDtos);
        } catch (Exception e) {
            log.error("Error fetching Kenya fee structures: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve Kenya fee structures: " + e.getMessage());
        }
    }

    // Get capitation grants by school
    public ApiResponse<List<CapitationGrantDto>> getCapitationGrantsBySchool(Long schoolId) {
        try {
            log.info("Fetching capitation grants for school: {}", schoolId);
            List<CapitationGrant> grants = capitationGrantRepository.findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(schoolId);
            List<CapitationGrantDto> grantDtos = grants.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("Capitation grants retrieved successfully", grantDtos);
        } catch (Exception e) {
            log.error("Error fetching capitation grants: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve capitation grants: " + e.getMessage());
        }
    }

    // Helper conversion methods
    private NemisStudentDto convertToDto(NemisStudent nemisStudent) {
        return NemisStudentDto.builder()
                .id(nemisStudent.getId())
                .studentId(nemisStudent.getStudent().getId())
                .studentName(nemisStudent.getStudent().getFirstName() + " " + nemisStudent.getStudent().getLastName())
                .nemisNumber(nemisStudent.getNemisNumber())
                .upiNumber(nemisStudent.getUpiNumber())
                .birthCertificateNumber(nemisStudent.getBirthCertificateNumber())
                .nationalIdNumber(nemisStudent.getNationalIdNumber())
                .county(nemisStudent.getCounty())
                .subCounty(nemisStudent.getSubCounty())
                .ward(nemisStudent.getWard())
                .constituency(nemisStudent.getConstituency())
                .disabilityStatus(nemisStudent.getDisabilityStatus().name())
                .disabilityDescription(nemisStudent.getDisabilityDescription())
                .orphanStatus(nemisStudent.getOrphanStatus().name())
                .vulnerableStatus(nemisStudent.getVulnerableStatus().name())
                .isSpecialNeeds(nemisStudent.getIsSpecialNeeds())
                .specialNeedsDescription(nemisStudent.getSpecialNeedsDescription())
                .isOrphan(nemisStudent.getIsOrphan())
                .isVulnerable(nemisStudent.getIsVulnerable())
                .isBursaryRecipient(nemisStudent.getIsBursaryRecipient())
                .bursarySource(nemisStudent.getBursarySource())
                .isCapitationRecipient(nemisStudent.getIsCapitationRecipient())
                .isNemisSynced(nemisStudent.getIsNemisSynced())
                .lastNemisSync(nemisStudent.getLastNemisSync())
                .isActive(nemisStudent.getIsActive())
                .createdAt(nemisStudent.getCreatedAt())
                .updatedAt(nemisStudent.getUpdatedAt())
                .build();
    }

    private CbcCompetencyDto convertToDto(CbcCompetency competency) {
        return CbcCompetencyDto.builder()
                .id(competency.getId())
                .competencyCode(competency.getCompetencyCode())
                .competencyName(competency.getCompetencyName())
                .description(competency.getDescription())
                .gradeLevel(competency.getGradeLevel().name())
                .learningArea(competency.getLearningArea().name())
                .strand(competency.getStrand().name())
                .subStrand(competency.getSubStrand().name())
                .sequence(competency.getSequence())
                .isCoreCompetency(competency.getIsCoreCompetency())
                .isActive(competency.getIsActive())
                .createdAt(competency.getCreatedAt())
                .updatedAt(competency.getUpdatedAt())
                .build();
    }

    private StudentCompetencyDto convertToDto(StudentCompetency studentCompetency) {
        return StudentCompetencyDto.builder()
                .id(studentCompetency.getId())
                .studentId(studentCompetency.getStudent().getId())
                .studentName(studentCompetency.getStudent().getFirstName() + " " + studentCompetency.getStudent().getLastName())
                .competencyId(studentCompetency.getCompetency().getId())
                .competencyName(studentCompetency.getCompetency().getCompetencyName())
                .competencyCode(studentCompetency.getCompetency().getCompetencyCode())
                .teacherId(studentCompetency.getTeacher().getId())
                .teacherName(studentCompetency.getTeacher().getFirstName() + " " + studentCompetency.getTeacher().getLastName())
                .academicYearId(studentCompetency.getAcademicYear().getId())
                .academicYearName(studentCompetency.getAcademicYear().getName())
                .termId(studentCompetency.getTerm().getId())
                .termName(studentCompetency.getTerm().getName())
                .level(studentCompetency.getLevel().name())
                .assessmentType(studentCompetency.getAssessmentType().name())
                .evidence(studentCompetency.getEvidence())
                .teacherComments(studentCompetency.getTeacherComments())
                .nextSteps(studentCompetency.getNextSteps())
                .isActive(studentCompetency.getIsActive())
                .createdAt(studentCompetency.getCreatedAt())
                .updatedAt(studentCompetency.getUpdatedAt())
                .build();
    }

    private KenyaFeeStructureDto convertToDto(KenyaFeeStructure feeStructure) {
        return KenyaFeeStructureDto.builder()
                .id(feeStructure.getId())
                .schoolId(feeStructure.getSchool().getId())
                .schoolName(feeStructure.getSchool().getName())
                .classId(feeStructure.getClassEntity().getId())
                .className(feeStructure.getClassEntity().getName())
                .academicYearId(feeStructure.getAcademicYear().getId())
                .academicYearName(feeStructure.getAcademicYear().getName())
                .feeName(feeStructure.getFeeName())
                .feeCode(feeStructure.getFeeCode())
                .feeType(feeStructure.getFeeType().name())
                .amount(feeStructure.getAmount())
                .capitationAmount(feeStructure.getCapitationAmount())
                .parentContribution(feeStructure.getParentContribution())
                .frequency(feeStructure.getFrequency().name())
                .isMandatory(feeStructure.getIsMandatory())
                .isCapitationEligible(feeStructure.getIsCapitationEligible())
                .isBursaryEligible(feeStructure.getIsBursaryEligible())
                .isActive(feeStructure.getIsActive())
                .createdAt(feeStructure.getCreatedAt())
                .updatedAt(feeStructure.getUpdatedAt())
                .build();
    }

    private CapitationGrantDto convertToDto(CapitationGrant grant) {
        return CapitationGrantDto.builder()
                .id(grant.getId())
                .schoolId(grant.getSchool().getId())
                .schoolName(grant.getSchool().getName())
                .academicYearId(grant.getAcademicYear().getId())
                .academicYearName(grant.getAcademicYear().getName())
                .grantNumber(grant.getGrantNumber())
                .totalAmount(grant.getTotalAmount())
                .receivedAmount(grant.getReceivedAmount())
                .pendingAmount(grant.getPendingAmount())
                .status(grant.getStatus().name())
                .expectedDate(grant.getExpectedDate())
                .receivedDate(grant.getReceivedDate() != null ? grant.getReceivedDate() : null)
                .studentCount(grant.getStudentCount())
                .perStudentAmount(grant.getPerStudentAmount())
                .remarks(grant.getRemarks())
                .isActive(grant.getIsActive())
                .createdAt(grant.getCreatedAt())
                .updatedAt(grant.getUpdatedAt())
                .build();
    }
}
