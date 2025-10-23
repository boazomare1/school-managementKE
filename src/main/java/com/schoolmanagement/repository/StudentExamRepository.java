package com.schoolmanagement.repository;

import com.schoolmanagement.entity.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {

    List<StudentExam> findByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(Long studentId);

    List<StudentExam> findByExamIdAndIsActiveTrueOrderByCreatedAtDesc(Long examId);

    List<StudentExam> findByClassEntityIdAndIsActiveTrueOrderByCreatedAtDesc(Long classId);

    List<StudentExam> findBySubjectIdAndIsActiveTrueOrderByCreatedAtDesc(Long subjectId);

    @Query("SELECT se FROM StudentExam se WHERE se.student.id = :studentId AND se.exam.id = :examId AND se.isActive = true")
    StudentExam findByStudentAndExam(Long studentId, Long examId);

    @Query("SELECT se FROM StudentExam se WHERE se.exam.id = :examId AND se.classEntity.id = :classId AND se.isActive = true")
    List<StudentExam> findByExamAndClass(Long examId, Long classId);

    @Query("SELECT se FROM StudentExam se WHERE se.student.id = :studentId AND se.subject.id = :subjectId AND se.isActive = true ORDER BY se.createdAt DESC")
    List<StudentExam> findByStudentAndSubject(Long studentId, Long subjectId);

    @Query("SELECT se FROM StudentExam se WHERE se.student.id = :studentId AND se.status = 'COMPLETED' AND se.isActive = true ORDER BY se.submittedAt DESC")
    List<StudentExam> findCompletedExamsByStudent(Long studentId);

    @Query("SELECT AVG(se.score) FROM StudentExam se WHERE se.student.id = :studentId AND se.status = 'COMPLETED' AND se.isActive = true")
    Double getAverageScoreByStudent(Long studentId);

    @Query("SELECT se FROM StudentExam se WHERE se.exam.id = :examId AND se.status = :status AND se.isActive = true")
    List<StudentExam> findByExamAndStatus(Long examId, StudentExam.ExamStatus status);
}

