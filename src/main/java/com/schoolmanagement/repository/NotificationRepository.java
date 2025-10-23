package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdAndIsActiveTrueOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(Long schoolId);

    List<Notification> findByClassEntityIdAndIsActiveTrueOrderByCreatedAtDesc(Long classId);

    List<Notification> findByStatusAndScheduledAtLessThanEqual(Notification.NotificationStatus status, LocalDateTime now);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :recipientId AND n.status = 'SENT' AND n.readAt IS NULL")
    Long countUnreadByRecipientId(Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.status = 'SENT' AND n.readAt IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByRecipientId(Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.school.id = :schoolId AND n.status = 'SENT' AND n.readAt IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findUnreadBySchoolId(Long schoolId);
}