package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.AttendanceRequestDto;
import com.schoolmanagement.dto.ExamResultRequestDto;
import com.schoolmanagement.dto.GradeTransitionRequestDto;
import com.schoolmanagement.dto.StudentAttendanceDto;
import com.schoolmanagement.dto.StudentEnrollmentRequestDto;
import com.schoolmanagement.dto.StudentExamDto;
import com.schoolmanagement.dto.StudentFeeDto;
import com.schoolmanagement.dto.StudentGradeDto;
import com.schoolmanagement.dto.StudentSubjectDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentLifecycleService {

    private final StudentGradeRepository studentGradeRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentAttendanceRepository studentAttendanceRepository;
    private final StudentExamRepository studentExamRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicYearRepository academicYearRepository;
    private final TermRepository termRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final ExamRepository examRepository;

    // Student Grade Assignment
    @Transactional
    public ApiResponse<StudentGradeDto> enrollStudentInGrade(StudentEnrollmentRequestDto request) {
        try {
            log.info("Enrolling student {} in class {}", request.getStudentId(), request.getClassId());

            // Validate entities exist
            User student = userRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            ClassEntity classEntity = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new RuntimeException("Academic year not found"));

            // Check if student is already enrolled in this class for this academic year
            List<StudentGrade> existingEnrollments = studentGradeRepository.findByStudentAndAcademicYear(
                    request.getStudentId(), request.getAcademicYearId());
            if (existingEnrollments.stream().anyMatch(sg -> sg.getClassEntity().getId().equals(request.getClassId()))) {
                return ApiResponse.error("Student is already enrolled in this class for this academic year");
            }

            // Create student grade enrollment
            StudentGrade studentGrade = StudentGrade.builder()
                    .student(student)
                    .classEntity(classEntity)
                    .academicYear(academicYear)
                    .status(StudentGrade.EnrollmentStatus.ENROLLED)
                    .enrollmentDate(request.getEnrollmentDate() != null ? request.getEnrollmentDate() : LocalDateTime.now())
                    .notes(request.getNotes())
                    .isActive(true)
                    .build();

            StudentGrade savedGrade = studentGradeRepository.save(studentGrade);

            // Enroll in subjects if specified
            if (request.getSubjectIds() != null && !request.getSubjectIds().isEmpty()) {
                for (Long subjectId : request.getSubjectIds()) {
                    Subject subject = subjectRepository.findById(subjectId)
                            .orElseThrow(() -> new RuntimeException("Subject not found"));
                    
                    StudentSubject studentSubject = StudentSubject.builder()
                            .student(student)
                            .subject(subject)
                            .classEntity(classEntity)
                            .academicYear(academicYear)
                            .status(StudentSubject.EnrollmentStatus.ENROLLED)
                            .enrollmentDate(LocalDateTime.now())
                            .isActive(true)
                            .build();
                    
                    studentSubjectRepository.save(studentSubject);
                }
            }

            return ApiResponse.success("Student enrolled successfully", convertToDto(savedGrade));

        } catch (Exception e) {
            log.error("Error enrolling student: {}", e.getMessage());
            return ApiResponse.error("Failed to enroll student: " + e.getMessage());
        }
    }

    // Attendance Management
    @Transactional
    public ApiResponse<StudentAttendanceDto> markAttendance(AttendanceRequestDto request, User markedBy) {
        try {
            log.info("Marking attendance for student {} on {}", request.getStudentId(), request.getAttendanceDate());

            // Validate entities
            User student = userRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            ClassEntity classEntity = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            Subject subject = null;
            if (request.getSubjectId() != null) {
                subject = subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("Subject not found"));
            }

            // Check if attendance already exists for this date
            List<StudentAttendance> existingAttendance = studentAttendanceRepository.findByStudentAndDate(
                    request.getStudentId(), request.getAttendanceDate());
            if (!existingAttendance.isEmpty()) {
                return ApiResponse.error("Attendance already marked for this student on this date");
            }

            StudentAttendance attendance = StudentAttendance.builder()
                    .student(student)
                    .classEntity(classEntity)
                    .subject(subject)
                    .attendanceDate(request.getAttendanceDate())
                    .status(StudentAttendance.AttendanceStatus.valueOf(request.getStatus()))
                    .reason(request.getReason())
                    .notes(request.getNotes())
                    .markedBy(markedBy)
                    .markedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();

            StudentAttendance savedAttendance = studentAttendanceRepository.save(attendance);
            return ApiResponse.success("Attendance marked successfully", convertToDto(savedAttendance));

        } catch (Exception e) {
            log.error("Error marking attendance: {}", e.getMessage());
            return ApiResponse.error("Failed to mark attendance: " + e.getMessage());
        }
    }

    // Exam Management
    @Transactional
    public ApiResponse<StudentExamDto> recordExamResult(ExamResultRequestDto request, User gradedBy) {
        try {
            log.info("Recording exam result for student {} in exam {}", request.getStudentId(), request.getExamId());

            // Validate entities
            User student = userRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Exam exam = examRepository.findById(request.getExamId())
                    .orElseThrow(() -> new RuntimeException("Exam not found"));
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ClassEntity classEntity = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            // Check if exam result already exists
            StudentExam existingExam = studentExamRepository.findByStudentAndExam(request.getStudentId(), request.getExamId());
            if (existingExam != null) {
                return ApiResponse.error("Exam result already recorded for this student");
            }

            StudentExam studentExam = StudentExam.builder()
                    .student(student)
                    .exam(exam)
                    .subject(subject)
                    .classEntity(classEntity)
                    .status(StudentExam.ExamStatus.COMPLETED)
                    .score(request.getScore())
                    .grade(request.getGrade())
                    .remarks(request.getRemarks())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .submittedAt(request.getSubmittedAt() != null ? request.getSubmittedAt() : LocalDateTime.now())
                    .gradedBy(gradedBy)
                    .gradedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();

            StudentExam savedExam = studentExamRepository.save(studentExam);
            return ApiResponse.success("Exam result recorded successfully", convertToDto(savedExam));

        } catch (Exception e) {
            log.error("Error recording exam result: {}", e.getMessage());
            return ApiResponse.error("Failed to record exam result: " + e.getMessage());
        }
    }

    // Fee Management
    @Transactional
    public ApiResponse<StudentFeeDto> createStudentFee(Long studentId, Long feeStructureId, Long termId) {
        try {
            log.info("Creating fee for student {} with fee structure {}", studentId, feeStructureId);

            // Validate entities
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            FeeStructure feeStructure = feeStructureRepository.findById(feeStructureId)
                    .orElseThrow(() -> new RuntimeException("Fee structure not found"));
            Term term = termRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));

            // Get current enrollment
            StudentGrade currentEnrollment = studentGradeRepository.findCurrentEnrollmentByStudent(studentId);
            if (currentEnrollment == null) {
                return ApiResponse.error("Student is not enrolled in any class");
            }

            StudentFee studentFee = StudentFee.builder()
                    .student(student)
                    .feeStructure(feeStructure)
                    .classEntity(currentEnrollment.getClassEntity())
                    .academicYear(currentEnrollment.getAcademicYear())
                    .term(term)
                    .amount(feeStructure.getAmount())
                    .paidAmount(BigDecimal.ZERO)
                    .balanceAmount(feeStructure.getAmount())
                    .status(StudentFee.FeeStatus.PENDING)
                    .dueDate(LocalDateTime.now().plusDays(30)) // 30 days from now
                    .isActive(true)
                    .build();

            StudentFee savedFee = studentFeeRepository.save(studentFee);
            return ApiResponse.success("Student fee created successfully", convertToDto(savedFee));

        } catch (Exception e) {
            log.error("Error creating student fee: {}", e.getMessage());
            return ApiResponse.error("Failed to create student fee: " + e.getMessage());
        }
    }

    // Grade Transition
    @Transactional
    public ApiResponse<String> transitionStudents(GradeTransitionRequestDto request) {
        try {
            log.info("Transitioning {} students from class {} to class {}", 
                    request.getStudentIds().size(), request.getFromClassId(), request.getToClassId());

            ClassEntity fromClass = classRepository.findById(request.getFromClassId())
                    .orElseThrow(() -> new RuntimeException("From class not found"));
            ClassEntity toClass = classRepository.findById(request.getToClassId())
                    .orElseThrow(() -> new RuntimeException("To class not found"));
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new RuntimeException("Academic year not found"));

            int transitionCount = 0;
            for (Long studentId : request.getStudentIds()) {
                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Student not found"));

                // Update current enrollment status
                StudentGrade currentEnrollment = studentGradeRepository.findCurrentEnrollmentByStudent(studentId);
                if (currentEnrollment != null) {
                    currentEnrollment.setStatus(StudentGrade.EnrollmentStatus.valueOf(request.getTransitionType()));
                    currentEnrollment.setCompletionDate(request.getTransitionDate() != null ? 
                            request.getTransitionDate() : LocalDateTime.now());
                    studentGradeRepository.save(currentEnrollment);
                }

                // Create new enrollment
                StudentGrade newEnrollment = StudentGrade.builder()
                        .student(student)
                        .classEntity(toClass)
                        .academicYear(academicYear)
                        .status(StudentGrade.EnrollmentStatus.ENROLLED)
                        .enrollmentDate(request.getTransitionDate() != null ? 
                                request.getTransitionDate() : LocalDateTime.now())
                        .notes(request.getNotes())
                        .isActive(true)
                        .build();

                studentGradeRepository.save(newEnrollment);
                transitionCount++;
            }

            return ApiResponse.success("Successfully transitioned " + transitionCount + " students", null);

        } catch (Exception e) {
            log.error("Error transitioning students: {}", e.getMessage());
            return ApiResponse.error("Failed to transition students: " + e.getMessage());
        }
    }

    // Get student information
    public ApiResponse<List<StudentGradeDto>> getStudentEnrollments(Long studentId) {
        try {
            log.info("Fetching enrollments for student: {}", studentId);
            List<StudentGrade> enrollments = studentGradeRepository.findByStudentIdAndIsActiveTrueOrderByEnrollmentDateDesc(studentId);
            List<StudentGradeDto> enrollmentDtos = enrollments.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("Student enrollments retrieved successfully", enrollmentDtos);
        } catch (Exception e) {
            log.error("Error fetching student enrollments: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve student enrollments: " + e.getMessage());
        }
    }

    public ApiResponse<List<StudentAttendanceDto>> getStudentAttendance(Long studentId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Fetching attendance for student: {} from {} to {}", studentId, startDate, endDate);
            List<StudentAttendance> attendance = studentAttendanceRepository.findByStudentAndDateRange(studentId, startDate, endDate);
            List<StudentAttendanceDto> attendanceDtos = attendance.stream()
                    .map(this::convertToDto)
                    .toList();
            return ApiResponse.success("Student attendance retrieved successfully", attendanceDtos);
        } catch (Exception e) {
            log.error("Error fetching student attendance: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve student attendance: " + e.getMessage());
        }
    }

    // Helper conversion methods
    private StudentGradeDto convertToDto(StudentGrade studentGrade) {
        return StudentGradeDto.builder()
                .id(studentGrade.getId())
                .studentId(studentGrade.getStudent().getId())
                .studentName(studentGrade.getStudent().getFirstName() + " " + studentGrade.getStudent().getLastName())
                .classId(studentGrade.getClassEntity().getId())
                .className(studentGrade.getClassEntity().getName())
                .academicYearId(studentGrade.getAcademicYear().getId())
                .academicYearName(studentGrade.getAcademicYear().getName())
                .status(studentGrade.getStatus().name())
                .enrollmentDate(studentGrade.getEnrollmentDate())
                .completionDate(studentGrade.getCompletionDate())
                .notes(studentGrade.getNotes())
                .isActive(studentGrade.getIsActive())
                .createdAt(studentGrade.getCreatedAt())
                .updatedAt(studentGrade.getUpdatedAt())
                .build();
    }

    private StudentAttendanceDto convertToDto(StudentAttendance attendance) {
        return StudentAttendanceDto.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName())
                .classId(attendance.getClassEntity().getId())
                .className(attendance.getClassEntity().getName())
                .subjectId(attendance.getSubject() != null ? attendance.getSubject().getId() : null)
                .subjectName(attendance.getSubject() != null ? attendance.getSubject().getName() : null)
                .teacherId(attendance.getTeacher() != null ? attendance.getTeacher().getId() : null)
                .teacherName(attendance.getTeacher() != null ? 
                        attendance.getTeacher().getFirstName() + " " + attendance.getTeacher().getLastName() : null)
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus().name())
                .reason(attendance.getReason())
                .notes(attendance.getNotes())
                .markedById(attendance.getMarkedBy().getId())
                .markedByName(attendance.getMarkedBy().getFirstName() + " " + attendance.getMarkedBy().getLastName())
                .markedAt(attendance.getMarkedAt())
                .isActive(attendance.getIsActive())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }

    private StudentExamDto convertToDto(StudentExam studentExam) {
        return StudentExamDto.builder()
                .id(studentExam.getId())
                .studentId(studentExam.getStudent().getId())
                .studentName(studentExam.getStudent().getFirstName() + " " + studentExam.getStudent().getLastName())
                .examId(studentExam.getExam().getId())
                .examName(studentExam.getExam().getName())
                .subjectId(studentExam.getSubject().getId())
                .subjectName(studentExam.getSubject().getName())
                .classId(studentExam.getClassEntity().getId())
                .className(studentExam.getClassEntity().getName())
                .status(studentExam.getStatus().name())
                .score(studentExam.getScore())
                .grade(studentExam.getGrade())
                .remarks(studentExam.getRemarks())
                .startTime(studentExam.getStartTime())
                .endTime(studentExam.getEndTime())
                .submittedAt(studentExam.getSubmittedAt())
                .gradedById(studentExam.getGradedBy() != null ? studentExam.getGradedBy().getId() : null)
                .gradedByName(studentExam.getGradedBy() != null ? 
                        studentExam.getGradedBy().getFirstName() + " " + studentExam.getGradedBy().getLastName() : null)
                .gradedAt(studentExam.getGradedAt())
                .isActive(studentExam.getIsActive())
                .createdAt(studentExam.getCreatedAt())
                .updatedAt(studentExam.getUpdatedAt())
                .build();
    }

    private StudentFeeDto convertToDto(StudentFee studentFee) {
        return StudentFeeDto.builder()
                .id(studentFee.getId())
                .studentId(studentFee.getStudent().getId())
                .studentName(studentFee.getStudent().getFirstName() + " " + studentFee.getStudent().getLastName())
                .feeStructureId(studentFee.getFeeStructure().getId())
                .feeStructureName(studentFee.getFeeStructure().getName())
                .classId(studentFee.getClassEntity().getId())
                .className(studentFee.getClassEntity().getName())
                .academicYearId(studentFee.getAcademicYear().getId())
                .academicYearName(studentFee.getAcademicYear().getName())
                .termId(studentFee.getTerm() != null ? studentFee.getTerm().getId() : null)
                .termName(studentFee.getTerm() != null ? studentFee.getTerm().getName() : null)
                .amount(studentFee.getAmount())
                .paidAmount(studentFee.getPaidAmount())
                .balanceAmount(studentFee.getBalanceAmount())
                .status(studentFee.getStatus().name())
                .dueDate(studentFee.getDueDate())
                .paidDate(studentFee.getPaidDate())
                .paymentReference(studentFee.getPaymentReference())
                .notes(studentFee.getNotes())
                .isActive(studentFee.getIsActive())
                .createdAt(studentFee.getCreatedAt())
                .updatedAt(studentFee.getUpdatedAt())
                .build();
    }
}
