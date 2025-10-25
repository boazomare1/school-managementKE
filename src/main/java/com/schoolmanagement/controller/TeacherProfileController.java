package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.TeacherDto;
import com.schoolmanagement.entity.ClassEntity;
import com.schoolmanagement.entity.Dormitory;
import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.entity.Notification;
import com.schoolmanagement.repository.ClassRepository;
import com.schoolmanagement.repository.DormitoryRepository;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.repository.UserRepository;
import com.schoolmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher-profiles")
@RequiredArgsConstructor
@Slf4j
public class TeacherProfileController {
    
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final DormitoryRepository dormitoryRepository;
    private final NotificationService notificationService;
    
    // Create teacher profile
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<TeacherDto>> createTeacherProfile(@Valid @RequestBody TeacherDto teacherDto) {
        try {
            log.info("Creating teacher profile for user ID: {}", teacherDto.getUserId());
            
            // Check if user exists
            Optional<User> userOptional = userRepository.findById(teacherDto.getUserId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
            }
            
            // Check if teacher profile already exists
            Optional<Teacher> existingTeacher = teacherRepository.findByUserId(teacherDto.getUserId());
            if (existingTeacher.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Teacher profile already exists for this user"));
            }
            
            // Check if TSC number already exists
            Optional<Teacher> existingTsc = teacherRepository.findByTscNumber(teacherDto.getTscNumber());
            if (existingTsc.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("TSC number already exists"));
            }
            
            User user = userOptional.get();
            
            // Get assigned class if provided
            ClassEntity assignedClass = null;
            if (teacherDto.getAssignedClassId() != null) {
                assignedClass = classRepository.findById(teacherDto.getAssignedClassId()).orElse(null);
            }
            
            // Get assigned dormitory if provided
            Dormitory assignedDormitory = null;
            if (teacherDto.getAssignedDormitoryId() != null) {
                assignedDormitory = dormitoryRepository.findById(teacherDto.getAssignedDormitoryId()).orElse(null);
            }
            
            // Create teacher entity
            Teacher teacher = Teacher.builder()
                    .user(user)
                    .tscNumber(teacherDto.getTscNumber())
                    .department(teacherDto.getDepartment())
                    .role(teacherDto.getRole())
                    .assignedClass(assignedClass)
                    .assignedDormitory(assignedDormitory)
                    .qualifications(teacherDto.getQualifications())
                    .experience(teacherDto.getExperience())
                    .notes(teacherDto.getNotes())
                    .isActive(true)
                    .build();
            
            Teacher savedTeacher = teacherRepository.save(teacher);
            TeacherDto responseDto = convertToDto(savedTeacher);
            
            log.info("Successfully created teacher profile with ID: {}", savedTeacher.getId());
            return ResponseEntity.ok(ApiResponse.success("Teacher profile created successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error creating teacher profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create teacher profile: " + e.getMessage()));
        }
    }
    
    // Get teacher profile by user ID
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<ApiResponse<TeacherDto>> getTeacherProfileByUserId(@PathVariable Long userId) {
        try {
            log.info("Fetching teacher profile for user ID: {}", userId);
            
            Optional<Teacher> teacher = teacherRepository.findByUserId(userId);
            if (teacher.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Teacher profile not found"));
            }
            
            TeacherDto responseDto = convertToDto(teacher.get());
            return ResponseEntity.ok(ApiResponse.success("Teacher profile retrieved successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error fetching teacher profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch teacher profile: " + e.getMessage()));
        }
    }
    
    // Get all teachers
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<TeacherDto>>> getAllTeachers() {
        try {
            log.info("Fetching all teachers");
            
            List<Teacher> teachers = teacherRepository.findByIsActiveTrue();
            List<TeacherDto> teacherDtos = teachers.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.success("Teachers retrieved successfully", teacherDtos));
            
        } catch (Exception e) {
            log.error("Error fetching teachers: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch teachers: " + e.getMessage()));
        }
    }
    
    // Update teacher profile
    @PutMapping("/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<TeacherDto>> updateTeacherProfile(@PathVariable Long teacherId, @Valid @RequestBody TeacherDto teacherDto) {
        try {
            log.info("Updating teacher profile with ID: {}", teacherId);
            
            Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);
            if (teacherOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Teacher profile not found"));
            }
            
            Teacher teacher = teacherOptional.get();
            
            // Update fields
            teacher.setTscNumber(teacherDto.getTscNumber());
            teacher.setDepartment(teacherDto.getDepartment());
            teacher.setRole(teacherDto.getRole());
            teacher.setQualifications(teacherDto.getQualifications());
            teacher.setExperience(teacherDto.getExperience());
            teacher.setNotes(teacherDto.getNotes());
            
            // Update assigned class if provided
            if (teacherDto.getAssignedClassId() != null) {
                ClassEntity assignedClass = classRepository.findById(teacherDto.getAssignedClassId()).orElse(null);
                teacher.setAssignedClass(assignedClass);
            }
            
            // Update assigned dormitory if provided
            if (teacherDto.getAssignedDormitoryId() != null) {
                Dormitory assignedDormitory = dormitoryRepository.findById(teacherDto.getAssignedDormitoryId()).orElse(null);
                teacher.setAssignedDormitory(assignedDormitory);
            }
            
            Teacher updatedTeacher = teacherRepository.save(teacher);
            TeacherDto responseDto = convertToDto(updatedTeacher);
            
            log.info("Successfully updated teacher profile with ID: {}", teacherId);
            return ResponseEntity.ok(ApiResponse.success("Teacher profile updated successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error updating teacher profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update teacher profile: " + e.getMessage()));
        }
    }
    
    // Assign class teacher
    @PutMapping("/{teacherId}/assign-class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<TeacherDto>> assignClassTeacher(@PathVariable Long teacherId, @PathVariable Long classId) {
        try {
            log.info("Assigning teacher {} as class teacher for class {}", teacherId, classId);
            
            Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);
            if (teacherOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Teacher not found"));
            }
            
            Optional<ClassEntity> classOptional = classRepository.findById(classId);
            if (classOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Class not found"));
            }
            
            Teacher teacher = teacherOptional.get();
            ClassEntity classEntity = classOptional.get();
            
            teacher.setAssignedClass(classEntity);
            teacher.setRole(Teacher.TeacherRole.CLASS_TEACHER);
            
            Teacher updatedTeacher = teacherRepository.save(teacher);
            TeacherDto responseDto = convertToDto(updatedTeacher);
            
            // Send assignment notification
            try {
                sendTeacherAssignmentNotification(teacher, classEntity, "CLASS_TEACHER");
            } catch (Exception e) {
                log.error("Error sending teacher assignment notification: {}", e.getMessage());
                // Don't fail assignment if notification fails
            }
            
            log.info("Successfully assigned teacher {} as class teacher for class {}", teacherId, classId);
            return ResponseEntity.ok(ApiResponse.success("Class teacher assigned successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error assigning class teacher: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to assign class teacher: " + e.getMessage()));
        }
    }
    
    // Assign dorm master
    @PutMapping("/{teacherId}/assign-dormitory/{dormitoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<TeacherDto>> assignDormMaster(@PathVariable Long teacherId, @PathVariable Long dormitoryId) {
        try {
            log.info("Assigning teacher {} as dorm master for dormitory {}", teacherId, dormitoryId);
            
            Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);
            if (teacherOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Teacher not found"));
            }
            
            Optional<Dormitory> dormitoryOptional = dormitoryRepository.findById(dormitoryId);
            if (dormitoryOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Dormitory not found"));
            }
            
            Teacher teacher = teacherOptional.get();
            Dormitory dormitory = dormitoryOptional.get();
            
            teacher.setAssignedDormitory(dormitory);
            
            Teacher updatedTeacher = teacherRepository.save(teacher);
            TeacherDto responseDto = convertToDto(updatedTeacher);
            
            // Send assignment notification
            try {
                sendTeacherAssignmentNotification(teacher, dormitory, "DORM_MASTER");
            } catch (Exception e) {
                log.error("Error sending teacher assignment notification: {}", e.getMessage());
                // Don't fail assignment if notification fails
            }
            
            log.info("Successfully assigned teacher {} as dorm master for dormitory {}", teacherId, dormitoryId);
            return ResponseEntity.ok(ApiResponse.success("Dorm master assigned successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error assigning dorm master: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to assign dorm master: " + e.getMessage()));
        }
    }
    
    // Convert entity to DTO
    private TeacherDto convertToDto(Teacher teacher) {
        try {
            User user = teacher.getUser();
            return TeacherDto.builder()
                    .id(teacher.getId())
                    .userId(user != null ? user.getId() : null)
                    .tscNumber(teacher.getTscNumber())
                    .department(teacher.getDepartment())
                    .role(teacher.getRole())
                    .assignedClassId(teacher.getAssignedClass() != null ? teacher.getAssignedClass().getId() : null)
                    .assignedDormitoryId(teacher.getAssignedDormitory() != null ? teacher.getAssignedDormitory().getId() : null)
                    .qualifications(teacher.getQualifications())
                    .experience(teacher.getExperience())
                    .notes(teacher.getNotes())
                    .isActive(teacher.getIsActive())
                    .createdAt(teacher.getCreatedAt())
                    .updatedAt(teacher.getUpdatedAt())
                    .username(user != null ? user.getUsername() : null)
                    .email(user != null ? user.getEmail() : null)
                    .firstName(user != null ? user.getFirstName() : null)
                    .lastName(user != null ? user.getLastName() : null)
                    .phoneNumber(user != null ? user.getPhoneNumber() : null)
                    .address(user != null ? user.getAddress() : null)
                    .build();
        } catch (Exception e) {
            log.error("Error converting teacher to DTO: {}", e.getMessage());
            // Return basic DTO without user details
            return TeacherDto.builder()
                    .id(teacher.getId())
                    .tscNumber(teacher.getTscNumber())
                    .department(teacher.getDepartment())
                    .role(teacher.getRole())
                    .assignedClassId(teacher.getAssignedClass() != null ? teacher.getAssignedClass().getId() : null)
                    .assignedDormitoryId(teacher.getAssignedDormitory() != null ? teacher.getAssignedDormitory().getId() : null)
                    .qualifications(teacher.getQualifications())
                    .experience(teacher.getExperience())
                    .notes(teacher.getNotes())
                    .isActive(teacher.getIsActive())
                    .createdAt(teacher.getCreatedAt())
                    .updatedAt(teacher.getUpdatedAt())
                    .build();
        }
    }
    
    // Send teacher assignment notification
    private void sendTeacherAssignmentNotification(Teacher teacher, Object assignment, String assignmentType) {
        try {
            User teacherUser = teacher.getUser();
            String assignmentName = "";
            String assignmentMessage = "";
            
            if ("CLASS_TEACHER".equals(assignmentType) && assignment instanceof ClassEntity) {
                ClassEntity classEntity = (ClassEntity) assignment;
                assignmentName = classEntity.getName();
                assignmentMessage = String.format(
                    "You have been assigned as the class teacher for %s. " +
                    "Your responsibilities include managing the class, monitoring student progress, " +
                    "and coordinating with parents. Please review your new assignment details.",
                    classEntity.getName()
                );
            } else if ("DORM_MASTER".equals(assignmentType) && assignment instanceof Dormitory) {
                Dormitory dormitory = (Dormitory) assignment;
                assignmentName = dormitory.getName();
                assignmentMessage = String.format(
                    "You have been assigned as the dorm master for %s. " +
                    "Your responsibilities include supervising students, maintaining dormitory discipline, " +
                    "and ensuring student welfare. Please review your new assignment details.",
                    dormitory.getName()
                );
            }
            
            com.schoolmanagement.dto.NotificationRequestDto notificationRequest = 
                com.schoolmanagement.dto.NotificationRequestDto.builder()
                    .title("New Teaching Assignment")
                    .message(assignmentMessage)
                    .type(Notification.NotificationType.TEACHER_ASSIGNMENT)
                    .priority(Notification.NotificationPriority.HIGH)
                    .recipientId(teacherUser.getId())
                    .actionUrl("/teacher/assignments")
                    .actionText("View Assignment Details")
                    .build();
            
            notificationService.createNotification(notificationRequest);
            log.info("Teacher assignment notification sent to: {}", teacherUser.getUsername());
            
        } catch (Exception e) {
            log.error("Error sending teacher assignment notification: {}", e.getMessage());
        }
    }
}
