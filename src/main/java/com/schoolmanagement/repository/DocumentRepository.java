package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUploadedByIdAndIsActiveTrueOrderByCreatedAtDesc(Long uploadedById);

    List<Document> findBySubjectIdAndIsActiveTrueOrderByCreatedAtDesc(Long subjectId);

    List<Document> findByClassEntityIdAndIsActiveTrueOrderByCreatedAtDesc(Long classId);

    List<Document> findByDocumentTypeAndIsActiveTrueOrderByCreatedAtDesc(Document.DocumentType documentType);

    List<Document> findByIsPublicTrueAndIsActiveTrueOrderByCreatedAtDesc();

    @Query("SELECT d FROM Document d WHERE d.subject.id = :subjectId AND d.classEntity.id = :classId AND d.isActive = true ORDER BY d.createdAt DESC")
    List<Document> findBySubjectAndClass(Long subjectId, Long classId);

    @Query("SELECT d FROM Document d WHERE d.assignment.id = :assignmentId AND d.isActive = true ORDER BY d.createdAt DESC")
    List<Document> findByAssignment(Long assignmentId);

    @Query("SELECT d FROM Document d WHERE d.exam.id = :examId AND d.isActive = true ORDER BY d.createdAt DESC")
    List<Document> findByExam(Long examId);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.uploadedBy.id = :teacherId AND d.isActive = true")
    Long countByTeacher(Long teacherId);

    @Query("SELECT d FROM Document d WHERE d.uploadedBy.id = :teacherId AND d.documentType = :documentType AND d.isActive = true ORDER BY d.createdAt DESC")
    List<Document> findByTeacherAndDocumentType(Long teacherId, Document.DocumentType documentType);
}

