package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.ClassDto;
import com.schoolmanagement.entity.AcademicYear;
import com.schoolmanagement.entity.ClassEntity;
import com.schoolmanagement.entity.School;
import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.repository.AcademicYearRepository;
import com.schoolmanagement.repository.ClassRepository;
import com.schoolmanagement.repository.SchoolRepository;
import com.schoolmanagement.repository.TeacherRepository;
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
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Slf4j
public class ClassController {

    private static final String CLASS_NOT_FOUND_MESSAGE = "Class not found";
    
    private final ClassRepository classRepository;
    private final SchoolRepository schoolRepository;
    private final AcademicYearRepository academicYearRepository;
    private final TeacherRepository teacherRepository;
    
    // Create class
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<ClassDto>> createClass(@Valid @RequestBody ClassDto classDto) {
        try {
            log.info("Creating class: {}", classDto.getName());
            
            // Check if school exists
            Optional<School> schoolOptional = schoolRepository.findById(classDto.getSchoolId());
            if (schoolOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("School not found"));
            }
            
            // Check if academic year exists
            Optional<AcademicYear> academicYearOptional = academicYearRepository.findById(classDto.getAcademicYearId());
            if (academicYearOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Academic year not found"));
            }
            
            // Check if class already exists
            Optional<ClassEntity> existingClass = classRepository.findBySchoolIdAndAcademicYearIdAndNameAndIsActiveTrue(
                    classDto.getSchoolId(), classDto.getAcademicYearId(), classDto.getName());
            if (existingClass.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Class already exists"));
            }
            
            School school = schoolOptional.get();
            AcademicYear academicYear = academicYearOptional.get();
            
            // Get class teacher if provided
            Teacher classTeacher = null;
            if (classDto.getClassTeacherId() != null) {
                classTeacher = teacherRepository.findById(classDto.getClassTeacherId()).orElse(null);
            }
            
            // Create class entity
            ClassEntity classEntity = new ClassEntity();
            classEntity.setName(classDto.getName());
            classEntity.setCode(classDto.getCode());
            classEntity.setSection(classDto.getSection());
            classEntity.setCapacity(classDto.getCapacity());
            classEntity.setDescription(classDto.getDescription());
            classEntity.setSchool(school);
            classEntity.setAcademicYear(academicYear);
            classEntity.setClassTeacher(classTeacher);
            classEntity.setIsActive(true);
            
            ClassEntity savedClass = classRepository.save(classEntity);
            ClassDto responseDto = convertToDto(savedClass);
            
            log.info("Successfully created class with ID: {}", savedClass.getId());
            return ResponseEntity.ok(ApiResponse.success("Class created successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error creating class: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create class: " + e.getMessage()));
        }
    }
    
    // Get all classes
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<ClassDto>>> getAllClasses() {
        try {
            log.info("Fetching all classes");
            
            List<ClassEntity> classes = classRepository.findAll().stream()
                    .filter(ClassEntity::getIsActive)
                    .toList();
            List<ClassDto> classDtos = classes.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.success("Classes retrieved successfully", classDtos));
            
        } catch (Exception e) {
            log.error("Error fetching classes: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch classes: " + e.getMessage()));
        }
    }
    
    // Get class by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<ClassDto>> getClassById(@PathVariable Long id) {
        try {
            log.info("Fetching class by ID: {}", id);
            
            Optional<ClassEntity> classOptional = classRepository.findById(id);
            if (classOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error(CLASS_NOT_FOUND_MESSAGE));
            }
            
            ClassDto responseDto = convertToDto(classOptional.get());
            return ResponseEntity.ok(ApiResponse.success("Class retrieved successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error fetching class: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch class: " + e.getMessage()));
        }
    }
    
    // Update class
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<ClassDto>> updateClass(@PathVariable Long id, @Valid @RequestBody ClassDto classDto) {
        try {
            log.info("Updating class with ID: {}", id);
            
            Optional<ClassEntity> classOptional = classRepository.findById(id);
            if (classOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error(CLASS_NOT_FOUND_MESSAGE));
            }
            
            ClassEntity classEntity = classOptional.get();
            
            // Update fields
            classEntity.setName(classDto.getName());
            classEntity.setCode(classDto.getCode());
            classEntity.setSection(classDto.getSection());
            classEntity.setCapacity(classDto.getCapacity());
            classEntity.setDescription(classDto.getDescription());
            
            // Update class teacher if provided
            if (classDto.getClassTeacherId() != null) {
                Teacher classTeacher = teacherRepository.findById(classDto.getClassTeacherId()).orElse(null);
                classEntity.setClassTeacher(classTeacher);
            }
            
            ClassEntity updatedClass = classRepository.save(classEntity);
            ClassDto responseDto = convertToDto(updatedClass);
            
            log.info("Successfully updated class with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success("Class updated successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error updating class: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update class: " + e.getMessage()));
        }
    }
    
    // Assign class teacher
    @PutMapping("/{id}/assign-teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<ClassDto>> assignClassTeacher(@PathVariable Long id, @PathVariable Long teacherId) {
        try {
            log.info("Assigning teacher {} as class teacher for class {}", teacherId, id);
            
            Optional<ClassEntity> classOptional = classRepository.findById(id);
            if (classOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error(CLASS_NOT_FOUND_MESSAGE));
            }
            
            Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);
            if (teacherOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Teacher not found"));
            }
            
            ClassEntity classEntity = classOptional.get();
            Teacher teacher = teacherOptional.get();
            
            classEntity.setClassTeacher(teacher);
            
            ClassEntity updatedClass = classRepository.save(classEntity);
            ClassDto responseDto = convertToDto(updatedClass);
            
            log.info("Successfully assigned teacher {} as class teacher for class {}", teacherId, id);
            return ResponseEntity.ok(ApiResponse.success("Class teacher assigned successfully", responseDto));
            
        } catch (Exception e) {
            log.error("Error assigning class teacher: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to assign class teacher: " + e.getMessage()));
        }
    }
    
    // Convert entity to DTO
    private ClassDto convertToDto(ClassEntity classEntity) {
        ClassDto dto = new ClassDto();
        dto.setId(classEntity.getId());
        dto.setName(classEntity.getName());
        dto.setCode(classEntity.getCode());
        dto.setSection(classEntity.getSection());
        dto.setCapacity(classEntity.getCapacity());
        dto.setDescription(classEntity.getDescription());
        dto.setIsActive(classEntity.getIsActive());
        dto.setSchoolId(classEntity.getSchool().getId());
        dto.setSchoolName(classEntity.getSchool().getName());
        dto.setAcademicYearId(classEntity.getAcademicYear().getId());
        dto.setAcademicYearName(classEntity.getAcademicYear().getName());
        dto.setClassTeacherId(classEntity.getClassTeacher() != null ? classEntity.getClassTeacher().getId() : null);
        dto.setClassTeacherName(classEntity.getClassTeacher() != null ? 
                classEntity.getClassTeacher().getUser().getFirstName() + " " + 
                classEntity.getClassTeacher().getUser().getLastName() : null);
        return dto;
    }
}
