package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByCode(String code);

    List<Subject> findByCurriculumType(Subject.CurriculumType curriculumType);

    List<Subject> findByCategory(Subject.SubjectCategory category);

    List<Subject> findByIsActiveTrue();

    List<Subject> findByCurriculumTypeAndIsActiveTrue(Subject.CurriculumType curriculumType);

    List<Subject> findByLearningAreaAndIsActiveTrue(String learningArea);

    List<Subject> findByFormLevelAndIsActiveTrue(String formLevel);
}