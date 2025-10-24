package com.schoolmanagement.repository;

import com.schoolmanagement.entity.NotificationTemplate;
import com.schoolmanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    
    Optional<NotificationTemplate> findByTemplateKeyAndIsActiveTrue(String templateKey);
    
    List<NotificationTemplate> findByTypeAndIsActiveTrue(Notification.NotificationType type);
    
    List<NotificationTemplate> findByIsActiveTrue();
}
