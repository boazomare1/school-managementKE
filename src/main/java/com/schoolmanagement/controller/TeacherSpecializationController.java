package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.Subject;
import com.schoolmanagement.entity.TeacherSpecialization;
import com.schoolmanagement.service.TeacherSpecializationService;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher-specializations")
@RequiredArgsConstructor
@Slf4j
public class TeacherSpecializationController {

    private final TeacherSpecializationService teacherSpecializationService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<?> addSpecialization(
            @RequestParam Long teacherId,
            @RequestParam Long subjectId,
            @RequestParam TeacherSpecialization.SpecializationLevel level,
            @RequestParam TeacherSpecialization.SpecializationType type) {
        try {
            TeacherSpecialization specialization = teacherSpecializationService.addSpecialization(teacherId, subjectId, level, type);
            return ResponseEntity.status(HttpStatus.CREATED).body(specialization);
        } catch (ResourceNotFoundException | ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding teacher specialization: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to add specialization"));
        }
    }

    @PostMapping("/add-interest")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<?> addInterest(
            @RequestParam Long teacherId,
            @RequestParam TeacherSpecialization.SpecializationType interestType,
            @RequestParam(required = false) String description) {
        try {
            TeacherSpecialization specialization = teacherSpecializationService.addInterest(teacherId, interestType, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(specialization);
        } catch (ResourceNotFoundException | ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding teacher interest: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to add interest"));
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<?> updateSpecialization(
            @PathVariable Long id,
            @RequestParam TeacherSpecialization.SpecializationLevel level) {
        try {
            TeacherSpecialization specialization = teacherSpecializationService.updateSpecialization(id, level);
            return ResponseEntity.ok(specialization);
        } catch (ResourceNotFoundException | ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating teacher specialization with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update specialization"));
        }
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<?> getTeacherSpecializations(@PathVariable Long teacherId) {
        try {
            List<TeacherSpecialization> specializations = teacherSpecializationService.getTeacherSpecializations(teacherId);
            return ResponseEntity.ok(specializations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching teacher specializations for teacher id {}: {}", teacherId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch specializations"));
        }
    }

    @GetMapping("/teacher/{teacherId}/level/{level}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<?> getTeacherSpecializationsByLevel(
            @PathVariable Long teacherId,
            @PathVariable TeacherSpecialization.SpecializationLevel level) {
        try {
            List<TeacherSpecialization> specializations = teacherSpecializationService.getTeacherSpecializationsByLevel(teacherId, level);
            return ResponseEntity.ok(specializations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching teacher specializations by level for teacher id {}: {}", teacherId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch specializations"));
        }
    }

    @GetMapping("/teacher/{teacherId}/subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<?> getSubjectsTeacherCanTeach(@PathVariable Long teacherId) {
        try {
            List<Subject> subjects = teacherSpecializationService.getSubjectsTeacherCanTeach(teacherId);
            return ResponseEntity.ok(subjects);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching subjects teacher can teach for teacher id {}: {}", teacherId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch subjects"));
        }
    }

    @GetMapping("/teacher/{teacherId}/interests")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<?> getTeacherInterests(@PathVariable Long teacherId) {
        try {
            List<TeacherSpecialization> interests = teacherSpecializationService.getTeacherInterests(teacherId);
            return ResponseEntity.ok(interests);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching teacher interests for teacher id {}: {}", teacherId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch interests"));
        }
    }

    @GetMapping("/interest/{interestType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<?> getTeachersByInterest(@PathVariable TeacherSpecialization.SpecializationType interestType) {
        try {
            List<TeacherSpecialization> teachers = teacherSpecializationService.getTeachersByInterest(interestType);
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            log.error("Error fetching teachers by interest {}: {}", interestType, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to fetch teachers by interest"));
        }
    }

    @GetMapping("/can-teach/{teacherId}/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER')")
    public ResponseEntity<?> canTeacherTeachSubject(@PathVariable Long teacherId, @PathVariable Long subjectId) {
        try {
            boolean canTeach = teacherSpecializationService.canTeacherTeachSubject(teacherId, subjectId);
            return ResponseEntity.ok(Map.of("canTeach", canTeach));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error checking if teacher can teach subject: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to check teaching capability"));
        }
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<?> removeSpecialization(@PathVariable Long id) {
        try {
            teacherSpecializationService.removeSpecialization(id);
            return ResponseEntity.ok(Map.of("message", "Specialization removed successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error removing teacher specialization with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to remove specialization"));
        }
    }
}
