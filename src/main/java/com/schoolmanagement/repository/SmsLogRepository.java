package com.schoolmanagement.repository;

import com.schoolmanagement.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {

    List<SmsLog> findBySchoolIdOrderByCreatedAtDesc(Long schoolId);

    List<SmsLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<SmsLog> findByStatusAndCreatedAtBetween(SmsLog.SmsStatus status, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM SmsLog s WHERE s.school.id = :schoolId AND s.status = 'SENT' AND s.createdAt >= :startDate")
    Long countSentSmsBySchoolAndDate(Long schoolId, LocalDateTime startDate);

    @Query("SELECT COUNT(s) FROM SmsLog s WHERE s.school.id = :schoolId AND s.status = 'FAILED' AND s.createdAt >= :startDate")
    Long countFailedSmsBySchoolAndDate(Long schoolId, LocalDateTime startDate);
}
