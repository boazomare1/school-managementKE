package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Notification;
import com.schoolmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByRecipientAndIsActiveTrueOrderByCreatedAtDesc(User recipient);
    
    Page<Notification> findByRecipientAndIsActiveTrueOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    List<Notification> findByRecipientAndReadAtIsNullAndIsActiveTrueOrderByCreatedAtDesc(User recipient);
    
    Long countByRecipientAndReadAtIsNullAndIsActiveTrue(User recipient);
    
    List<Notification> findByRecipientAndTypeAndIsActiveTrueOrderByCreatedAtDesc(User recipient, Notification.NotificationType type);
    
    Page<Notification> findByRecipientAndTypeAndIsActiveTrueOrderByCreatedAtDesc(User recipient, Notification.NotificationType type, Pageable pageable);
    
    List<Notification> findByRecipientAndPriorityAndIsActiveTrueOrderByCreatedAtDesc(User recipient, Notification.NotificationPriority priority);
    
    List<Notification> findByStatusAndIsActiveTrueOrderByCreatedAtAsc(Notification.NotificationStatus status);
    
    List<Notification> findByCreatedAtBeforeAndStatusAndIsActiveTrue(LocalDateTime before, Notification.NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.isActive = true AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(n.message) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByRecipientAndSearchTerm(@Param("recipient") User recipient, 
                                                   @Param("search") String search, 
                                                   Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.isActive = true AND " +
           "n.type = :type AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(n.message) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByRecipientAndTypeAndSearchTerm(@Param("recipient") User recipient,
                                                           @Param("type") Notification.NotificationType type,
                                                           @Param("search") String search,
                                                           Pageable pageable);
}