package com.schoolmanagement.repository;

import com.schoolmanagement.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {

    List<StudentFee> findByStudentIdAndIsActiveTrueOrderByDueDateDesc(Long studentId);

    List<StudentFee> findByClassEntityIdAndIsActiveTrueOrderByDueDateDesc(Long classId);

    List<StudentFee> findByAcademicYearIdAndIsActiveTrueOrderByDueDateDesc(Long academicYearId);

    List<StudentFee> findByStatusAndIsActiveTrue(StudentFee.FeeStatus status);

    @Query("SELECT sf FROM StudentFee sf WHERE sf.student.id = :studentId AND sf.academicYear.id = :academicYearId AND sf.isActive = true")
    List<StudentFee> findByStudentAndAcademicYear(Long studentId, Long academicYearId);

    @Query("SELECT sf FROM StudentFee sf WHERE sf.student.id = :studentId AND sf.term.id = :termId AND sf.isActive = true")
    List<StudentFee> findByStudentAndTerm(Long studentId, Long termId);

    @Query("SELECT sf FROM StudentFee sf WHERE sf.student.id = :studentId AND sf.status = 'PENDING' AND sf.isActive = true")
    List<StudentFee> findPendingFeesByStudent(Long studentId);

    @Query("SELECT sf FROM StudentFee sf WHERE sf.student.id = :studentId AND sf.status = 'OVERDUE' AND sf.isActive = true")
    List<StudentFee> findOverdueFeesByStudent(Long studentId);

    @Query("SELECT SUM(sf.balanceAmount) FROM StudentFee sf WHERE sf.student.id = :studentId AND sf.isActive = true")
    BigDecimal getTotalBalanceByStudent(Long studentId);

    @Query("SELECT SUM(sf.paidAmount) FROM StudentFee sf WHERE sf.student.id = :studentId AND sf.isActive = true")
    BigDecimal getTotalPaidByStudent(Long studentId);

    @Query("SELECT sf FROM StudentFee sf WHERE sf.dueDate <= :date AND sf.status IN ('PENDING', 'PARTIAL') AND sf.isActive = true")
    List<StudentFee> findOverdueFeesByDate(LocalDateTime date);
}

