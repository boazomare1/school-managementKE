package com.schoolmanagement.repository;

import com.schoolmanagement.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {

    List<StudentAttendance> findByStudentIdAndIsActiveTrueOrderByAttendanceDateDesc(Long studentId);

    List<StudentAttendance> findByClassEntityIdAndIsActiveTrueOrderByAttendanceDateDesc(Long classId);

    List<StudentAttendance> findBySubjectIdAndIsActiveTrueOrderByAttendanceDateDesc(Long subjectId);

    List<StudentAttendance> findByAttendanceDateAndIsActiveTrue(LocalDate attendanceDate);

    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.student.id = :studentId AND sa.attendanceDate = :date AND sa.isActive = true")
    List<StudentAttendance> findByStudentAndDate(Long studentId, LocalDate date);

    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.classEntity.id = :classId AND sa.attendanceDate = :date AND sa.isActive = true")
    List<StudentAttendance> findByClassAndDate(Long classId, LocalDate date);

    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.classEntity.id = :classId AND sa.subject.id = :subjectId AND sa.attendanceDate = :date AND sa.isActive = true")
    List<StudentAttendance> findByClassSubjectAndDate(Long classId, Long subjectId, LocalDate date);

    @Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.student.id = :studentId AND sa.status = 'PRESENT' AND sa.isActive = true")
    Long countPresentDaysByStudent(Long studentId);

    @Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.student.id = :studentId AND sa.isActive = true")
    Long countTotalDaysByStudent(Long studentId);

    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.student.id = :studentId AND sa.attendanceDate BETWEEN :startDate AND :endDate AND sa.isActive = true ORDER BY sa.attendanceDate")
    List<StudentAttendance> findByStudentAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate);
}

