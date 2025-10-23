package com.schoolmanagement.repository;

import com.schoolmanagement.entity.StudentRelationship;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.entity.ClassEntity;
import com.schoolmanagement.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRelationshipRepository extends JpaRepository<StudentRelationship, Long> {

    /**
     * Find student relationship by student ID
     */
    Optional<StudentRelationship> findByStudentIdAndIsActiveTrue(Long studentId);

    /**
     * Find all students in a specific class
     */
    List<StudentRelationship> findByCurrentClassIdAndIsActiveTrue(Long classId);

    /**
     * Find all students in a specific academic year
     */
    List<StudentRelationship> findByAcademicYearIdAndIsActiveTrue(Long academicYearId);

    /**
     * Find students by class teacher
     */
    List<StudentRelationship> findByClassTeacherIdAndIsActiveTrue(Long teacherId);

    /**
     * Find students by enrollment status
     */
    List<StudentRelationship> findByEnrollmentStatusAndIsActiveTrue(StudentRelationship.EnrollmentStatus status);

    /**
     * Find students by current grade
     */
    List<StudentRelationship> findByCurrentGradeAndIsActiveTrue(String grade);

    /**
     * Find students who are promoted
     */
    List<StudentRelationship> findByIsPromotedTrueAndIsActiveTrue();

    /**
     * Find students who are graduated
     */
    List<StudentRelationship> findByIsGraduatedTrueAndIsActiveTrue();

    /**
     * Find students by enrollment number
     */
    Optional<StudentRelationship> findByEnrollmentNumberAndIsActiveTrue(String enrollmentNumber);

    /**
     * Find students enrolled between dates
     */
    List<StudentRelationship> findByEnrollmentDateBetweenAndIsActiveTrue(
        LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find students by academic year progress
     */
    List<StudentRelationship> findByAcademicYearProgressAndIsActiveTrue(Integer progress);

    /**
     * Count students in a class
     */
    long countByCurrentClassIdAndIsActiveTrue(Long classId);

    /**
     * Count students in an academic year
     */
    long countByAcademicYearIdAndIsActiveTrue(Long academicYearId);

    /**
     * Find students with incomplete parent/guardian relationships
     */
    @Query("SELECT sr FROM StudentRelationship sr WHERE sr.isActive = true " +
           "AND (SELECT COUNT(pg) FROM ParentGuardian pg WHERE pg.student = sr.student AND pg.isActive = true) < 1")
    List<StudentRelationship> findStudentsWithIncompleteParentGuardianRelationships();

    /**
     * Find students with maximum parent/guardian relationships
     */
    @Query("SELECT sr FROM StudentRelationship sr WHERE sr.isActive = true " +
           "AND (SELECT COUNT(pg) FROM ParentGuardian pg WHERE pg.student = sr.student AND pg.isActive = true) >= 3")
    List<StudentRelationship> findStudentsWithMaxParentGuardianRelationships();

    /**
     * Find students without primary contact
     */
    @Query("SELECT sr FROM StudentRelationship sr WHERE sr.isActive = true " +
           "AND NOT EXISTS (SELECT pg FROM ParentGuardian pg WHERE pg.student = sr.student " +
           "AND pg.isActive = true AND pg.isPrimaryContact = true)")
    List<StudentRelationship> findStudentsWithoutPrimaryContact();

    /**
     * Find students without emergency contact
     */
    @Query("SELECT sr FROM StudentRelationship sr WHERE sr.isActive = true " +
           "AND NOT EXISTS (SELECT pg FROM ParentGuardian pg WHERE pg.student = sr.student " +
           "AND pg.isActive = true AND pg.isEmergencyContact = true)")
    List<StudentRelationship> findStudentsWithoutEmergencyContact();

    /**
     * Find students by multiple criteria
     */
    @Query("SELECT sr FROM StudentRelationship sr WHERE sr.isActive = true " +
           "AND (:classId IS NULL OR sr.currentClass.id = :classId) " +
           "AND (:academicYearId IS NULL OR sr.academicYear.id = :academicYearId) " +
           "AND (:teacherId IS NULL OR sr.classTeacher.id = :teacherId) " +
           "AND (:status IS NULL OR sr.enrollmentStatus = :status) " +
           "AND (:grade IS NULL OR sr.currentGrade = :grade)")
    List<StudentRelationship> findStudentsByMultipleCriteria(
        @Param("classId") Long classId,
        @Param("academicYearId") Long academicYearId,
        @Param("teacherId") Long teacherId,
        @Param("status") StudentRelationship.EnrollmentStatus status,
        @Param("grade") String grade
    );
}

