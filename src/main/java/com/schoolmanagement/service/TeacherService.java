package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.AssignmentDto;
import com.schoolmanagement.dto.AssignmentRequestDto;
import com.schoolmanagement.dto.AssignmentSubmissionDto;
import com.schoolmanagement.dto.AssignmentSubmissionRequestDto;
import com.schoolmanagement.dto.DocumentDto;
import com.schoolmanagement.dto.DocumentUploadRequestDto;
import com.schoolmanagement.dto.LessonPlanDto;
import com.schoolmanagement.dto.LessonPlanRequestDto;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

    private final DocumentRepository documentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final LessonPlanRepository lessonPlanRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ClassRepository classRepository;
    private final AcademicYearRepository academicYearRepository;
    private final TermRepository termRepository;
    private final ExamRepository examRepository;

    private static final String UPLOAD_DIR = "uploads/";

    // Document Management
    @Transactional
    public ApiResponse<DocumentDto> uploadDocument(MultipartFile file, DocumentUploadRequestDto request, User teacher) {
        try {
            log.info("Uploading document: {} by teacher: {}", file.getOriginalFilename(), teacher.getUsername());

            // Validate file
            if (file.isEmpty()) {
                return ApiResponse.error("File is empty");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Get related entities
            Subject subject = request.getSubjectId() != null ? 
                    subjectRepository.findById(request.getSubjectId()).orElse(null) : null;
            ClassEntity classEntity = request.getClassId() != null ? 
                    classRepository.findById(request.getClassId()).orElse(null) : null;
            AcademicYear academicYear = request.getAcademicYearId() != null ? 
                    academicYearRepository.findById(request.getAcademicYearId()).orElse(null) : null;
            Exam exam = request.getExamId() != null ? 
                    examRepository.findById(request.getExamId()).orElse(null) : null;
            Assignment assignment = request.getAssignmentId() != null ? 
                    assignmentRepository.findById(request.getAssignmentId()).orElse(null) : null;

            // Create document entity
            Document document = Document.builder()
                    .fileName(uniqueFilename)
                    .originalFileName(originalFilename)
                    .filePath(filePath.toString())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .documentType(Document.DocumentType.valueOf(request.getDocumentType()))
                    .uploadedBy(teacher)
                    .subject(subject)
                    .classEntity(classEntity)
                    .academicYear(academicYear)
                    .exam(exam)
                    .assignment(assignment)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .isPublic(request.getIsPublic())
                    .isActive(true)
                    .build();

            Document savedDocument = documentRepository.save(document);
            return ApiResponse.success("Document uploaded successfully", convertToDto(savedDocument));

        } catch (IOException e) {
            log.error("Error uploading document: {}", e.getMessage());
            return ApiResponse.error("Failed to upload document: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error uploading document: {}", e.getMessage());
            return ApiResponse.error("Failed to upload document: " + e.getMessage());
        }
    }

    // Assignment Management
    @Transactional
    public ApiResponse<AssignmentDto> createAssignment(AssignmentRequestDto request, User teacher) {
        try {
            log.info("Creating assignment: {} by teacher: {}", request.getTitle(), teacher.getUsername());

            // Validate entities
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ClassEntity classEntity = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new RuntimeException("Academic year not found"));

            Term term = null;
            if (request.getTermId() != null) {
                term = termRepository.findById(request.getTermId())
                        .orElseThrow(() -> new RuntimeException("Term not found"));
            }

            Assignment assignment = Assignment.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .subject(subject)
                    .classEntity(classEntity)
                    .teacher(teacher)
                    .academicYear(academicYear)
                    .term(term)
                    .assignedDate(LocalDateTime.now())
                    .dueDate(request.getDueDate())
                    .totalMarks(request.getTotalMarks())
                    .passingMarks(request.getPassingMarks())
                    .assignmentType(Assignment.AssignmentType.valueOf(request.getAssignmentType()))
                    .status(Assignment.AssignmentStatus.DRAFT)
                    .instructions(request.getInstructions())
                    .submissionFormat(request.getSubmissionFormat())
                    .maxAttempts(request.getMaxAttempts())
                    .allowLateSubmission(request.getAllowLateSubmission())
                    .latePenaltyPercentage(request.getLatePenaltyPercentage())
                    .isActive(true)
                    .build();

            Assignment savedAssignment = assignmentRepository.save(assignment);
            return ApiResponse.success("Assignment created successfully", convertToDto(savedAssignment));

        } catch (Exception e) {
            log.error("Error creating assignment: {}", e.getMessage());
            return ApiResponse.error("Failed to create assignment: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<AssignmentDto> publishAssignment(Long assignmentId, User teacher) {
        try {
            log.info("Publishing assignment: {} by teacher: {}", assignmentId, teacher.getUsername());

            Assignment assignment = assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new RuntimeException("Assignment not found"));

            if (!assignment.getTeacher().getId().equals(teacher.getId())) {
                return ApiResponse.error("You can only publish your own assignments");
            }

            assignment.setStatus(Assignment.AssignmentStatus.PUBLISHED);
            Assignment savedAssignment = assignmentRepository.save(assignment);
            return ApiResponse.success("Assignment published successfully", convertToDto(savedAssignment));

        } catch (Exception e) {
            log.error("Error publishing assignment: {}", e.getMessage());
            return ApiResponse.error("Failed to publish assignment: " + e.getMessage());
        }
    }

    // Assignment Submission Management
    @Transactional
    public ApiResponse<AssignmentSubmissionDto> submitAssignment(AssignmentSubmissionRequestDto request, User student) {
        try {
            log.info("Submitting assignment: {} by student: {}", request.getAssignmentId(), student.getUsername());

            Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                    .orElseThrow(() -> new RuntimeException("Assignment not found"));

            if (assignment.getStatus() != Assignment.AssignmentStatus.PUBLISHED) {
                return ApiResponse.error("Assignment is not available for submission");
            }

            // Check if assignment is overdue
            boolean isLate = LocalDateTime.now().isAfter(assignment.getDueDate());
            if (isLate && !assignment.getAllowLateSubmission()) {
                return ApiResponse.error("Assignment submission deadline has passed");
            }

            // Check existing submissions
            AssignmentSubmission existingSubmission = assignmentSubmissionRepository
                    .findByAssignmentAndStudent(request.getAssignmentId(), request.getStudentId());
            
            if (existingSubmission != null && existingSubmission.getAttemptNumber() >= assignment.getMaxAttempts()) {
                return ApiResponse.error("Maximum submission attempts exceeded");
            }

            AssignmentSubmission submission = AssignmentSubmission.builder()
                    .assignment(assignment)
                    .student(student)
                    .submittedAt(LocalDateTime.now())
                    .status(AssignmentSubmission.SubmissionStatus.SUBMITTED)
                    .submissionText(request.getSubmissionText())
                    .fileName(request.getFileName())
                    .fileType(request.getFileType())
                    .fileSize(request.getFileSize())
                    .isLate(isLate)
                    .attemptNumber(existingSubmission != null ? existingSubmission.getAttemptNumber() + 1 : 1)
                    .isActive(true)
                    .build();

            AssignmentSubmission savedSubmission = assignmentSubmissionRepository.save(submission);
            return ApiResponse.success("Assignment submitted successfully", convertToDto(savedSubmission));

        } catch (Exception e) {
            log.error("Error submitting assignment: {}", e.getMessage());
            return ApiResponse.error("Failed to submit assignment: " + e.getMessage());
        }
    }

    // Grading
    @Transactional
    public ApiResponse<AssignmentSubmissionDto> gradeSubmission(Long submissionId, BigDecimal score, String grade, String feedback, User teacher) {
        try {
            log.info("Grading submission: {} by teacher: {}", submissionId, teacher.getUsername());

            AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission not found"));

            if (!submission.getAssignment().getTeacher().getId().equals(teacher.getId())) {
                return ApiResponse.error("You can only grade submissions for your own assignments");
            }

            submission.setScore(score);
            submission.setGrade(grade);
            submission.setFeedback(feedback);
            submission.setStatus(AssignmentSubmission.SubmissionStatus.GRADED);
            submission.setGradedBy(teacher);
            submission.setGradedAt(LocalDateTime.now());

            AssignmentSubmission savedSubmission = assignmentSubmissionRepository.save(submission);
            return ApiResponse.success("Submission graded successfully", convertToDto(savedSubmission));

        } catch (Exception e) {
            log.error("Error grading submission: {}", e.getMessage());
            return ApiResponse.error("Failed to grade submission: " + e.getMessage());
        }
    }

    // Lesson Plan Management
    @Transactional
    public ApiResponse<LessonPlanDto> createLessonPlan(LessonPlanRequestDto request, User teacher) {
        try {
            log.info("Creating lesson plan: {} by teacher: {}", request.getTitle(), teacher.getUsername());

            // Validate entities
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ClassEntity classEntity = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new RuntimeException("Academic year not found"));

            Term term = null;
            if (request.getTermId() != null) {
                term = termRepository.findById(request.getTermId())
                        .orElseThrow(() -> new RuntimeException("Term not found"));
            }

            LessonPlan lessonPlan = LessonPlan.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .subject(subject)
                    .classEntity(classEntity)
                    .teacher(teacher)
                    .academicYear(academicYear)
                    .term(term)
                    .lessonDate(request.getLessonDate())
                    .duration(request.getDuration())
                    .objectives(request.getObjectives())
                    .materials(request.getMaterials())
                    .activities(request.getActivities())
                    .homework(request.getHomework())
                    .assessment(request.getAssessment())
                    .notes(request.getNotes())
                    .status(LessonPlan.LessonStatus.DRAFT)
                    .isActive(true)
                    .build();

            LessonPlan savedLessonPlan = lessonPlanRepository.save(lessonPlan);
            return ApiResponse.success("Lesson plan created successfully", convertToDto(savedLessonPlan));

        } catch (Exception e) {
            log.error("Error creating lesson plan: {}", e.getMessage());
            return ApiResponse.error("Failed to create lesson plan: " + e.getMessage());
        }
    }

    // Get teacher's assignments
    public ApiResponse<List<AssignmentDto>> getTeacherAssignments(Long teacherId) {
        try {
            log.info("Fetching assignments for teacher: {}", teacherId);
            List<Assignment> assignments = assignmentRepository.findByTeacherIdAndIsActiveTrueOrderByAssignedDateDesc(teacherId);
            List<AssignmentDto> assignmentDtos = assignments.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ApiResponse.success("Teacher assignments retrieved successfully", assignmentDtos);
        } catch (Exception e) {
            log.error("Error fetching teacher assignments: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve teacher assignments: " + e.getMessage());
        }
    }

    // Get assignment submissions
    public ApiResponse<List<AssignmentSubmissionDto>> getAssignmentSubmissions(Long assignmentId) {
        try {
            log.info("Fetching submissions for assignment: {}", assignmentId);
            List<AssignmentSubmission> submissions = assignmentSubmissionRepository.findByAssignmentIdAndIsActiveTrueOrderBySubmittedAtDesc(assignmentId);
            List<AssignmentSubmissionDto> submissionDtos = submissions.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ApiResponse.success("Assignment submissions retrieved successfully", submissionDtos);
        } catch (Exception e) {
            log.error("Error fetching assignment submissions: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve assignment submissions: " + e.getMessage());
        }
    }

    // Helper conversion methods
    private DocumentDto convertToDto(Document document) {
        return DocumentDto.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .originalFileName(document.getOriginalFileName())
                .filePath(document.getFilePath())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .documentType(document.getDocumentType().name())
                .uploadedById(document.getUploadedBy().getId())
                .uploadedByName(document.getUploadedBy().getFirstName() + " " + document.getUploadedBy().getLastName())
                .subjectId(document.getSubject() != null ? document.getSubject().getId() : null)
                .subjectName(document.getSubject() != null ? document.getSubject().getName() : null)
                .classId(document.getClassEntity() != null ? document.getClassEntity().getId() : null)
                .className(document.getClassEntity() != null ? document.getClassEntity().getName() : null)
                .academicYearId(document.getAcademicYear() != null ? document.getAcademicYear().getId() : null)
                .academicYearName(document.getAcademicYear() != null ? document.getAcademicYear().getName() : null)
                .examId(document.getExam() != null ? document.getExam().getId() : null)
                .examName(document.getExam() != null ? document.getExam().getName() : null)
                .assignmentId(document.getAssignment() != null ? document.getAssignment().getId() : null)
                .assignmentTitle(document.getAssignment() != null ? document.getAssignment().getTitle() : null)
                .title(document.getTitle())
                .description(document.getDescription())
                .isPublic(document.getIsPublic())
                .isActive(document.getIsActive())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private AssignmentDto convertToDto(Assignment assignment) {
        return AssignmentDto.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .subjectId(assignment.getSubject().getId())
                .subjectName(assignment.getSubject().getName())
                .classId(assignment.getClassEntity().getId())
                .className(assignment.getClassEntity().getName())
                .teacherId(assignment.getTeacher().getId())
                .teacherName(assignment.getTeacher().getFirstName() + " " + assignment.getTeacher().getLastName())
                .academicYearId(assignment.getAcademicYear().getId())
                .academicYearName(assignment.getAcademicYear().getName())
                .termId(assignment.getTerm() != null ? assignment.getTerm().getId() : null)
                .termName(assignment.getTerm() != null ? assignment.getTerm().getName() : null)
                .assignedDate(assignment.getAssignedDate())
                .dueDate(assignment.getDueDate())
                .totalMarks(assignment.getTotalMarks())
                .passingMarks(assignment.getPassingMarks())
                .assignmentType(assignment.getAssignmentType().name())
                .status(assignment.getStatus().name())
                .instructions(assignment.getInstructions())
                .submissionFormat(assignment.getSubmissionFormat())
                .maxAttempts(assignment.getMaxAttempts())
                .allowLateSubmission(assignment.getAllowLateSubmission())
                .latePenaltyPercentage(assignment.getLatePenaltyPercentage())
                .isActive(assignment.getIsActive())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }

    private AssignmentSubmissionDto convertToDto(AssignmentSubmission submission) {
        return AssignmentSubmissionDto.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .assignmentTitle(submission.getAssignment().getTitle())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getFirstName() + " " + submission.getStudent().getLastName())
                .submittedAt(submission.getSubmittedAt())
                .status(submission.getStatus().name())
                .submissionText(submission.getSubmissionText())
                .filePath(submission.getFilePath())
                .fileName(submission.getFileName())
                .fileType(submission.getFileType())
                .fileSize(submission.getFileSize())
                .score(submission.getScore())
                .grade(submission.getGrade())
                .feedback(submission.getFeedback())
                .isLate(submission.getIsLate())
                .attemptNumber(submission.getAttemptNumber())
                .gradedById(submission.getGradedBy() != null ? submission.getGradedBy().getId() : null)
                .gradedByName(submission.getGradedBy() != null ? 
                        submission.getGradedBy().getFirstName() + " " + submission.getGradedBy().getLastName() : null)
                .gradedAt(submission.getGradedAt())
                .isActive(submission.getIsActive())
                .createdAt(submission.getCreatedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }

    private LessonPlanDto convertToDto(LessonPlan lessonPlan) {
        return LessonPlanDto.builder()
                .id(lessonPlan.getId())
                .title(lessonPlan.getTitle())
                .description(lessonPlan.getDescription())
                .subjectId(lessonPlan.getSubject().getId())
                .subjectName(lessonPlan.getSubject().getName())
                .classId(lessonPlan.getClassEntity().getId())
                .className(lessonPlan.getClassEntity().getName())
                .teacherId(lessonPlan.getTeacher().getId())
                .teacherName(lessonPlan.getTeacher().getFirstName() + " " + lessonPlan.getTeacher().getLastName())
                .academicYearId(lessonPlan.getAcademicYear().getId())
                .academicYearName(lessonPlan.getAcademicYear().getName())
                .termId(lessonPlan.getTerm() != null ? lessonPlan.getTerm().getId() : null)
                .termName(lessonPlan.getTerm() != null ? lessonPlan.getTerm().getName() : null)
                .lessonDate(lessonPlan.getLessonDate())
                .duration(lessonPlan.getDuration())
                .objectives(lessonPlan.getObjectives())
                .materials(lessonPlan.getMaterials())
                .activities(lessonPlan.getActivities())
                .homework(lessonPlan.getHomework())
                .assessment(lessonPlan.getAssessment())
                .notes(lessonPlan.getNotes())
                .status(lessonPlan.getStatus().name())
                .isActive(lessonPlan.getIsActive())
                .createdAt(lessonPlan.getCreatedAt())
                .updatedAt(lessonPlan.getUpdatedAt())
                .build();
    }
}

