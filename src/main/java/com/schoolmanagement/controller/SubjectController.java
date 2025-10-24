package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.Subject;
import com.schoolmanagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Slf4j
public class SubjectController {
    
    private final SubjectRepository subjectRepository;
    
    // Get all subjects
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<Subject>>> getAllSubjects() {
        try {
            log.info("Fetching all subjects");
            
            List<Subject> subjects = subjectRepository.findByIsActiveTrue();
            
            return ResponseEntity.ok(ApiResponse.success("Subjects retrieved successfully", subjects));
            
        } catch (Exception e) {
            log.error("Error fetching subjects: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch subjects: " + e.getMessage()));
        }
    }
    
    // Create basic subjects if they don't exist
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> initializeSubjects() {
        try {
            log.info("Initializing basic subjects");
            
            // Create Mathematics
            if (subjectRepository.findByCode("MATH").isEmpty()) {
                Subject math = new Subject();
                math.setCode("MATH");
                math.setName("Mathematics");
                math.setCurriculumType(Subject.CurriculumType.EIGHT_FOUR_FOUR);
                math.setCategory(Subject.SubjectCategory.CORE);
                math.setDescription("Core Mathematics subject");
                math.setFormLevel("Form 1-4");
                math.setIsActive(true);
                subjectRepository.save(math);
            }
            
            // Create Chemistry
            if (subjectRepository.findByCode("CHEM").isEmpty()) {
                Subject chem = new Subject();
                chem.setCode("CHEM");
                chem.setName("Chemistry");
                chem.setCurriculumType(Subject.CurriculumType.EIGHT_FOUR_FOUR);
                chem.setCategory(Subject.SubjectCategory.SCIENTIFIC);
                chem.setDescription("Chemistry subject");
                chem.setFormLevel("Form 1-4");
                chem.setIsActive(true);
                subjectRepository.save(chem);
            }
            
            // Create Business Studies
            if (subjectRepository.findByCode("BUS").isEmpty()) {
                Subject bus = new Subject();
                bus.setCode("BUS");
                bus.setName("Business Studies");
                bus.setCurriculumType(Subject.CurriculumType.EIGHT_FOUR_FOUR);
                bus.setCategory(Subject.SubjectCategory.ELECTIVE);
                bus.setDescription("Business Studies subject");
                bus.setFormLevel("Form 1-4");
                bus.setIsActive(true);
                subjectRepository.save(bus);
            }
            
            // Create Physical Health Education
            if (subjectRepository.findByCode("PHE").isEmpty()) {
                Subject phe = new Subject();
                phe.setCode("PHE");
                phe.setName("Physical Health Education");
                phe.setCurriculumType(Subject.CurriculumType.EIGHT_FOUR_FOUR);
                phe.setCategory(Subject.SubjectCategory.PHYSICAL);
                phe.setDescription("Physical Health Education subject");
                phe.setFormLevel("Form 1-4");
                phe.setIsActive(true);
                subjectRepository.save(phe);
            }
            
            return ResponseEntity.ok(ApiResponse.success("Subjects initialized successfully"));
            
        } catch (Exception e) {
            log.error("Error initializing subjects: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to initialize subjects: " + e.getMessage()));
        }
    }
}
