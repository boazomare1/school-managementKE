package com.schoolmanagement.service;

import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.entity.Subject;
import com.schoolmanagement.entity.TeacherSpecialization;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.exception.ValidationException;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.repository.SubjectRepository;
import com.schoolmanagement.repository.TeacherSpecializationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherSpecializationService {

    private final TeacherSpecializationRepository teacherSpecializationRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    private static final int MAX_PRIMARY_SPECIALIZATIONS = 2;
    private static final int MAX_SECONDARY_SPECIALIZATIONS = 2;

    @Transactional
    public TeacherSpecialization addSpecialization(Long teacherId, Long subjectId, TeacherSpecialization.SpecializationLevel level, TeacherSpecialization.SpecializationType type) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        // Check if teacher already has this subject specialization
        if (teacherSpecializationRepository.findByTeacherAndSubject(teacher, subject).isPresent()) {
            throw new ValidationException("Teacher already has specialization in this subject");
        }

        // Validate specialization limits
        validateSpecializationLimits(teacher, level);

        TeacherSpecialization specialization = TeacherSpecialization.builder()
                .teacher(teacher)
                .subject(subject)
                .level(level)
                .type(type)
                .isActive(true)
                .build();

        return teacherSpecializationRepository.save(specialization);
    }

    @Transactional
    public TeacherSpecialization addInterest(Long teacherId, TeacherSpecialization.SpecializationType interestType, String description) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        // For interests, we don't need a subject - create a generic subject or use null
        // For now, we'll create a specialization without a specific subject
        TeacherSpecialization specialization = TeacherSpecialization.builder()
                .teacher(teacher)
                .subject(null) // Interests don't require specific subjects
                .level(TeacherSpecialization.SpecializationLevel.INTEREST)
                .type(interestType)
                .isActive(true)
                .notes(description)
                .build();

        return teacherSpecializationRepository.save(specialization);
    }

    @Transactional
    public TeacherSpecialization updateSpecialization(Long id, TeacherSpecialization.SpecializationLevel newLevel) {
        TeacherSpecialization specialization = teacherSpecializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherSpecialization not found with id: " + id));

        // Validate new level limits
        validateSpecializationLimits(specialization.getTeacher(), newLevel);

        specialization.setLevel(newLevel);
        return teacherSpecializationRepository.save(specialization);
    }

    @Transactional(readOnly = true)
    public List<TeacherSpecialization> getTeacherSpecializations(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        return teacherSpecializationRepository.findByTeacherAndIsActiveTrue(teacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherSpecialization> getTeacherSpecializationsByLevel(Long teacherId, TeacherSpecialization.SpecializationLevel level) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        return teacherSpecializationRepository.findByTeacherAndLevelAndIsActiveTrue(teacher, level);
    }

    @Transactional(readOnly = true)
    public List<TeacherSpecialization> getTeacherInterests(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        return teacherSpecializationRepository.findByTeacherAndLevelAndIsActiveTrue(teacher, TeacherSpecialization.SpecializationLevel.INTEREST);
    }

    @Transactional(readOnly = true)
    public List<TeacherSpecialization> getTeachersByInterest(TeacherSpecialization.SpecializationType interestType) {
        return teacherSpecializationRepository.findByTypeAndLevelAndIsActiveTrue(interestType, TeacherSpecialization.SpecializationLevel.INTEREST);
    }

    @Transactional(readOnly = true)
    public List<Subject> getSubjectsTeacherCanTeach(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        List<TeacherSpecialization> specializations = teacherSpecializationRepository.findByTeacherAndIsActiveTrue(teacher);
        return specializations.stream()
                .map(TeacherSpecialization::getSubject)
                .toList();
    }

    @Transactional
    public void removeSpecialization(Long id) {
        TeacherSpecialization specialization = teacherSpecializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherSpecialization not found with id: " + id));

        specialization.setIsActive(false);
        teacherSpecializationRepository.save(specialization);
    }

    @Transactional(readOnly = true)
    public boolean canTeacherTeachSubject(Long teacherId, Long subjectId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        return teacherSpecializationRepository.findByTeacherAndSubject(teacher, subject)
                .map(TeacherSpecialization::getIsActive)
                .orElse(false);
    }

    private void validateSpecializationLimits(Teacher teacher, TeacherSpecialization.SpecializationLevel level) {
        long currentCount = teacherSpecializationRepository.countByTeacherAndLevelAndIsActiveTrue(teacher, level);
        
        if (level == TeacherSpecialization.SpecializationLevel.PRIMARY && currentCount >= MAX_PRIMARY_SPECIALIZATIONS) {
            throw new ValidationException("Teacher cannot have more than " + MAX_PRIMARY_SPECIALIZATIONS + " primary specializations");
        }
        
        if (level == TeacherSpecialization.SpecializationLevel.SECONDARY && currentCount >= MAX_SECONDARY_SPECIALIZATIONS) {
            throw new ValidationException("Teacher cannot have more than " + MAX_SECONDARY_SPECIALIZATIONS + " secondary specializations");
        }
    }
}
