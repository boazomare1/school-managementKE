package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementType type; // GENERAL, CLASS_SPECIFIC, PARENT_ONLY, TEACHER_ONLY, STUDENT_ONLY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementPriority priority; // LOW, MEDIUM, HIGH, URGENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @Column(nullable = false)
    private LocalDateTime publishDate;

    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isPublished = false;

    @Column(columnDefinition = "TEXT")
    private String attachments; // JSON string for file attachments

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum AnnouncementType {
        GENERAL,
        CLASS_SPECIFIC,
        PARENT_ONLY,
        TEACHER_ONLY,
        STUDENT_ONLY
    }

    public enum AnnouncementPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}


