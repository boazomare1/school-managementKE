package com.schoolmanagement.service;

import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ClassRepository classRepository;
    private final AcademicYearRepository academicYearRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    private static final String UPLOAD_DIR = "uploads/";
    private static final String OFFLINE_DIR = "offline/";

    public Resource getVideoResource(Document document) {
        try {
            Path filePath = Paths.get(document.getFilePath());
            if (!Files.exists(filePath)) {
                return null;
            }
            return new UrlResource(filePath.toUri());
        } catch (Exception e) {
            log.error("Error getting video resource: {}", e.getMessage());
            return null;
        }
    }

    public Resource getDocumentResource(Document document) {
        try {
            Path filePath = Paths.get(document.getFilePath());
            if (!Files.exists(filePath)) {
                return null;
            }
            return new UrlResource(filePath.toUri());
        } catch (Exception e) {
            log.error("Error getting document resource: {}", e.getMessage());
            return null;
        }
    }

    public boolean hasAccessToDocument(User user, Document document) {
        // Check if user is the uploader
        if (document.getUploadedBy().getId().equals(user.getId())) {
            return true;
        }

        // Check if user is a student and document is public
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("STUDENT"))) {
            return document.getIsPublic();
        }

        // Check if user is a teacher and has access to the subject/class
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("TEACHER"))) {
            // Teachers have access to documents in their assigned classes
            return true; // Simplified for now
        }

        // Admin has access to all documents
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            return true;
        }

        return false;
    }

    public String prepareOfflineContent(Document document, User user) {
        try {
            // Create offline directory for user
            Path userOfflineDir = Paths.get(OFFLINE_DIR, user.getUsername());
            if (!Files.exists(userOfflineDir)) {
                Files.createDirectories(userOfflineDir);
            }

            // Copy file to offline directory
            Path sourcePath = Paths.get(document.getFilePath());
            Path offlinePath = userOfflineDir.resolve(document.getFileName());
            
            if (!Files.exists(offlinePath)) {
                Files.copy(sourcePath, offlinePath);
            }

            // Create metadata file for offline access
            String metadata = String.format(
                "{\n" +
                "  \"documentId\": %d,\n" +
                "  \"title\": \"%s\",\n" +
                "  \"description\": \"%s\",\n" +
                "  \"fileName\": \"%s\",\n" +
                "  \"fileType\": \"%s\",\n" +
                "  \"downloadDate\": \"%s\",\n" +
                "  \"offlinePath\": \"%s\"\n" +
                "}",
                document.getId(),
                document.getTitle(),
                document.getDescription(),
                document.getOriginalFileName(),
                document.getFileType(),
                LocalDateTime.now(),
                offlinePath.toString()
            );

            Path metadataPath = userOfflineDir.resolve(document.getId() + "_metadata.json");
            Files.write(metadataPath, metadata.getBytes());

            return "/api/media/offline/" + user.getUsername() + "/" + document.getFileName();

        } catch (Exception e) {
            log.error("Error preparing offline content: {}", e.getMessage());
            throw new RuntimeException("Failed to prepare offline content", e);
        }
    }

    public List<Object> getStudentProgress(Long studentId, User user) {
        try {
            // Get student enrollments
            List<StudentGrade> enrollments = studentGradeRepository.findByStudentIdAndIsActiveTrueOrderByEnrollmentDateDesc(studentId);
            
            List<Object> progress = new ArrayList<>();
            
            for (StudentGrade enrollment : enrollments) {
                // Get documents for this class
                List<Document> documents = documentRepository.findByClassEntityIdAndIsActiveTrueOrderByCreatedAtDesc(enrollment.getClassEntity().getId());
                
                Map<String, Object> classProgress = new HashMap<>();
                classProgress.put("classId", enrollment.getClassEntity().getId());
                classProgress.put("className", enrollment.getClassEntity().getName());
                classProgress.put("totalDocuments", documents.size());
                
                // Calculate progress metrics
                long videoCount = documents.stream()
                    .filter(doc -> doc.getFileType().toLowerCase().contains("video"))
                    .count();
                
                long pdfCount = documents.stream()
                    .filter(doc -> doc.getFileType().toLowerCase().contains("pdf"))
                    .count();
                
                long assignmentCount = documents.stream()
                    .filter(doc -> doc.getDocumentType() == Document.DocumentType.ASSIGNMENT)
                    .count();
                
                classProgress.put("videoCount", videoCount);
                classProgress.put("pdfCount", pdfCount);
                classProgress.put("assignmentCount", assignmentCount);
                classProgress.put("completionRate", calculateCompletionRate(documents, studentId));
                
                progress.add(classProgress);
            }
            
            return progress;
            
        } catch (Exception e) {
            log.error("Error getting student progress: {}", e.getMessage());
            throw new RuntimeException("Failed to get student progress", e);
        }
    }

    private double calculateCompletionRate(List<Document> documents, Long studentId) {
        // This would typically involve checking student interactions with documents
        // For now, return a placeholder value
        return Math.random() * 100; // Placeholder implementation
    }

    public Object uploadVideo(MultipartFile file, String title, String description, 
                            Long subjectId, Long classId, Long academicYearId, User user) {
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                throw new IllegalArgumentException("File must be a video");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Get related entities
            Subject subject = subjectRepository.findById(subjectId).orElse(null);
            ClassEntity classEntity = classRepository.findById(classId).orElse(null);
            AcademicYear academicYear = academicYearRepository.findById(academicYearId).orElse(null);

            // Create document entity
            Document document = Document.builder()
                    .fileName(uniqueFilename)
                    .originalFileName(originalFilename)
                    .filePath(filePath.toString())
                    .fileType(contentType)
                    .fileSize(file.getSize())
                    .documentType(Document.DocumentType.LESSON_NOTES)
                    .uploadedBy(user)
                    .subject(subject)
                    .classEntity(classEntity)
                    .academicYear(academicYear)
                    .title(title)
                    .description(description)
                    .isPublic(true)
                    .isActive(true)
                    .build();

            Document savedDocument = documentRepository.save(document);
            log.info("Video uploaded successfully: {}", savedDocument.getId());

            return Map.of(
                "documentId", savedDocument.getId(),
                "fileName", savedDocument.getOriginalFileName(),
                "filePath", savedDocument.getFilePath(),
                "streamUrl", "/api/media/stream/" + savedDocument.getId()
            );

        } catch (Exception e) {
            log.error("Error uploading video: {}", e.getMessage());
            throw new RuntimeException("Failed to upload video", e);
        }
    }
}
