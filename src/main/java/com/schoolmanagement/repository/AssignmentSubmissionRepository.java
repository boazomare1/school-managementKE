package com.schoolmanagement.repository;

import com.schoolmanagement.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    List<AssignmentSubmission> findByStudentIdAndIsActiveTrueOrderBySubmittedAtDesc(Long studentId);

    List<AssignmentSubmission> findByAssignmentIdAndIsActiveTrueOrderBySubmittedAtDesc(Long assignmentId);

    List<AssignmentSubmission> findByStatusAndIsActiveTrue(AssignmentSubmission.SubmissionStatus status);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.student.id = :studentId AND s.isActive = true")
    AssignmentSubmission findByAssignmentAndStudent(Long assignmentId, Long studentId);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.teacher.id = :teacherId AND s.isActive = true ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findByTeacher(Long teacherId);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.status = :status AND s.isActive = true")
    List<AssignmentSubmission> findByAssignmentAndStatus(Long assignmentId, AssignmentSubmission.SubmissionStatus status);

    @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.isActive = true")
    Long countByAssignment(Long assignmentId);

    @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.status = 'SUBMITTED' AND s.isActive = true")
    Long countSubmittedByAssignment(Long assignmentId);

    @Query("SELECT AVG(s.score) FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.score IS NOT NULL AND s.isActive = true")
    BigDecimal getAverageScoreByAssignment(Long assignmentId);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.isLate = true AND s.isActive = true")
    List<AssignmentSubmission> findLateSubmissionsByAssignment(Long assignmentId);
}

