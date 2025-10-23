package com.schoolmanagement.repository;

import com.schoolmanagement.entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    List<StudentSubject> findByStudentIdAndIsActiveTrueOrderByEnrollmentDateDesc(Long studentId);

    List<StudentSubject> findByClassEntityIdAndIsActiveTrueOrderByEnrollmentDateDesc(Long classId);

    List<StudentSubject> findBySubjectIdAndIsActiveTrueOrderByEnrollmentDateDesc(Long subjectId);

    List<StudentSubject> findByTeacherIdAndIsActiveTrueOrderByEnrollmentDateDesc(Long teacherId);

    @Query("SELECT ss FROM StudentSubject ss WHERE ss.student.id = :studentId AND ss.classEntity.id = :classId AND ss.isActive = true")
    List<StudentSubject> findByStudentAndClass(Long studentId, Long classId);

    @Query("SELECT ss FROM StudentSubject ss WHERE ss.student.id = :studentId AND ss.academicYear.id = :academicYearId AND ss.isActive = true")
    List<StudentSubject> findByStudentAndAcademicYear(Long studentId, Long academicYearId);

    @Query("SELECT ss FROM StudentSubject ss WHERE ss.classEntity.id = :classId AND ss.subject.id = :subjectId AND ss.isActive = true")
    List<StudentSubject> findByClassAndSubject(Long classId, Long subjectId);

    @Query("SELECT COUNT(ss) FROM StudentSubject ss WHERE ss.subject.id = :subjectId AND ss.status = 'ENROLLED' AND ss.isActive = true")
    Long countEnrolledStudentsBySubject(Long subjectId);
}

