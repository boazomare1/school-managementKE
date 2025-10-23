package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findBySchoolIdAndIsActiveTrueAndIsPublishedTrueOrderByPublishDateDesc(Long schoolId);

    List<Announcement> findByClassEntityIdAndIsActiveTrueAndIsPublishedTrueOrderByPublishDateDesc(Long classId);

    List<Announcement> findByAuthorIdAndIsActiveTrueOrderByCreatedAtDesc(Long authorId);

    @Query("SELECT a FROM Announcement a WHERE a.school.id = :schoolId AND a.isActive = true AND a.isPublished = true AND a.publishDate <= :now AND (a.expiryDate IS NULL OR a.expiryDate > :now) ORDER BY a.publishDate DESC")
    List<Announcement> findActiveBySchoolId(Long schoolId, LocalDateTime now);

    @Query("SELECT a FROM Announcement a WHERE a.classEntity.id = :classId AND a.isActive = true AND a.isPublished = true AND a.publishDate <= :now AND (a.expiryDate IS NULL OR a.expiryDate > :now) ORDER BY a.publishDate DESC")
    List<Announcement> findActiveByClassId(Long classId, LocalDateTime now);

    @Query("SELECT a FROM Announcement a WHERE a.school.id = :schoolId AND a.isActive = true AND a.isPublished = false ORDER BY a.createdAt DESC")
    List<Announcement> findDraftsBySchoolId(Long schoolId);
}