package com.schoolmanagement.repository;

import com.schoolmanagement.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

    List<EmailLog> findBySchoolIdOrderByCreatedAtDesc(Long schoolId);

    List<EmailLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<EmailLog> findByStatusAndCreatedAtBetween(EmailLog.EmailStatus status, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.school.id = :schoolId AND e.status = 'SENT' AND e.createdAt >= :startDate")
    Long countSentEmailsBySchoolAndDate(Long schoolId, LocalDateTime startDate);

    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.school.id = :schoolId AND e.status = 'FAILED' AND e.createdAt >= :startDate")
    Long countFailedEmailsBySchoolAndDate(Long schoolId, LocalDateTime startDate);
}