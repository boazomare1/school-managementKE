package com.schoolmanagement.repository;

import com.schoolmanagement.entity.TeacherSpecialization;
import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSpecializationRepository extends JpaRepository<TeacherSpecialization, Long> {

    List<TeacherSpecialization> findByTeacher(Teacher teacher);

    List<TeacherSpecialization> findByTeacherAndIsActiveTrue(Teacher teacher);

    List<TeacherSpecialization> findBySubject(Subject subject);

    List<TeacherSpecialization> findByTeacherAndLevel(Teacher teacher, TeacherSpecialization.SpecializationLevel level);

    Optional<TeacherSpecialization> findByTeacherAndSubject(Teacher teacher, Subject subject);

    List<TeacherSpecialization> findByTeacherAndLevelAndIsActiveTrue(Teacher teacher, TeacherSpecialization.SpecializationLevel level);

    List<TeacherSpecialization> findByTypeAndLevelAndIsActiveTrue(TeacherSpecialization.SpecializationType type, TeacherSpecialization.SpecializationLevel level);

    long countByTeacherAndLevelAndIsActiveTrue(Teacher teacher, TeacherSpecialization.SpecializationLevel level);
}
