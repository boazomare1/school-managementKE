package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByEnrollmentId(Long enrollmentId);
    
    List<Attendance> findByClassSubjectIdAndDate(Long classSubjectId, LocalDate date);
    
    @Query("SELECT AVG(CASE WHEN a.status = 'Present' THEN 1.0 ELSE 0.0 END) FROM Attendance a WHERE a.enrollment.classEntity.school.id = :schoolId")
    Double getAverageAttendanceBySchool(Long schoolId);
    
    @Query("SELECT a.date, COUNT(CASE WHEN a.status = 'Present' THEN 1 END) as present, COUNT(*) as total FROM Attendance a WHERE a.enrollment.classEntity.school.id = :schoolId GROUP BY a.date ORDER BY a.date")
    List<Object[]> getAttendanceTrendBySchool(Long schoolId);
}
