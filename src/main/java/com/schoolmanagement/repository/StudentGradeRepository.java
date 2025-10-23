package com.schoolmanagement.repository;

import com.schoolmanagement.entity.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {

    List<StudentGrade> findByStudentIdAndIsActiveTrueOrderByEnrollmentDateDesc(Long studentId);

    List<StudentGrade> findByClassEntityIdAndIsActiveTrueOrderByEnrollmentDateDesc(Long classId);

    List<StudentGrade> findByAcademicYearIdAndIsActiveTrueOrderByEnrollmentDateDesc(Long academicYearId);

    List<StudentGrade> findByStatusAndIsActiveTrue(StudentGrade.EnrollmentStatus status);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.student.id = :studentId AND sg.academicYear.id = :academicYearId AND sg.isActive = true")
    List<StudentGrade> findByStudentAndAcademicYear(Long studentId, Long academicYearId);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.classEntity.id = :classId AND sg.academicYear.id = :academicYearId AND sg.isActive = true")
    List<StudentGrade> findByClassAndAcademicYear(Long classId, Long academicYearId);

    @Query("SELECT COUNT(sg) FROM StudentGrade sg WHERE sg.classEntity.id = :classId AND sg.status = 'ENROLLED' AND sg.isActive = true")
    Long countEnrolledStudentsByClass(Long classId);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.student.id = :studentId AND sg.status = 'ENROLLED' AND sg.isActive = true")
    StudentGrade findCurrentEnrollmentByStudent(Long studentId);
}

