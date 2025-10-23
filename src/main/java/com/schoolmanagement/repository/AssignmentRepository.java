package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByTeacherIdAndIsActiveTrueOrderByAssignedDateDesc(Long teacherId);

    List<Assignment> findByClassEntityIdAndIsActiveTrueOrderByAssignedDateDesc(Long classId);

    List<Assignment> findBySubjectIdAndIsActiveTrueOrderByAssignedDateDesc(Long subjectId);

    List<Assignment> findByStatusAndIsActiveTrue(Assignment.AssignmentStatus status);

    @Query("SELECT a FROM Assignment a WHERE a.classEntity.id = :classId AND a.subject.id = :subjectId AND a.isActive = true ORDER BY a.assignedDate DESC")
    List<Assignment> findByClassAndSubject(Long classId, Long subjectId);

    @Query("SELECT a FROM Assignment a WHERE a.teacher.id = :teacherId AND a.status = :status AND a.isActive = true ORDER BY a.assignedDate DESC")
    List<Assignment> findByTeacherAndStatus(Long teacherId, Assignment.AssignmentStatus status);

    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :currentDate AND a.status = 'PUBLISHED' AND a.isActive = true")
    List<Assignment> findOverdueAssignments(LocalDateTime currentDate);

    @Query("SELECT a FROM Assignment a WHERE a.teacher.id = :teacherId AND a.assignmentType = :assignmentType AND a.isActive = true ORDER BY a.assignedDate DESC")
    List<Assignment> findByTeacherAndAssignmentType(Long teacherId, Assignment.AssignmentType assignmentType);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.teacher.id = :teacherId AND a.isActive = true")
    Long countByTeacher(Long teacherId);
}

