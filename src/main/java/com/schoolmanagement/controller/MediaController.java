package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.Document;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.DocumentRepository;
import com.schoolmanagement.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final MediaService mediaService;
    private final DocumentRepository documentRepository;

    @GetMapping("/stream/{documentId}")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long documentId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            log.info("Streaming video for document: {} by user: {}", documentId, user.getUsername());

            Optional<Document> document = documentRepository.findById(documentId);
            if (document.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Check if user has access to this document
            if (!mediaService.hasAccessToDocument(user, document.get())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Resource resource = mediaService.getVideoResource(document.get());
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(Paths.get(document.get().getFilePath()));
            if (contentType == null) {
                contentType = "video/mp4"; // Default for video files
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.get().getOriginalFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error streaming video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            log.info("Downloading document: {} by user: {}", documentId, user.getUsername());

            Optional<Document> document = documentRepository.findById(documentId);
            if (document.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Check if user has access to this document
            if (!mediaService.hasAccessToDocument(user, document.get())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Resource resource = mediaService.getDocumentResource(document.get());
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.get().getOriginalFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error downloading document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/offline/{documentId}")
    public ResponseEntity<ApiResponse<String>> prepareOfflineContent(@PathVariable Long documentId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            log.info("Preparing offline content for document: {} by user: {}", documentId, user.getUsername());

            Optional<Document> document = documentRepository.findById(documentId);
            if (document.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Document not found"));
            }

            if (!mediaService.hasAccessToDocument(user, document.get())) {
                return ResponseEntity.ok(ApiResponse.error("Access denied"));
            }

            String offlineUrl = mediaService.prepareOfflineContent(document.get(), user);
            return ResponseEntity.ok(ApiResponse.success("Offline content prepared", offlineUrl));

        } catch (Exception e) {
            log.error("Error preparing offline content: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to prepare offline content: " + e.getMessage()));
        }
    }

    @GetMapping("/progress/{studentId}")
    public ResponseEntity<ApiResponse<List<Object>>> getStudentProgress(@PathVariable Long studentId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            log.info("Getting progress for student: {} by user: {}", studentId, user.getUsername());

            List<Object> progress = mediaService.getStudentProgress(studentId, user);
            return ResponseEntity.ok(ApiResponse.success("Student progress retrieved", progress));

        } catch (Exception e) {
            log.error("Error getting student progress: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to get student progress: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-video")
    public ResponseEntity<ApiResponse<Object>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("classId") Long classId,
            @RequestParam("academicYearId") Long academicYearId,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            log.info("Uploading video: {} by user: {}", file.getOriginalFilename(), user.getUsername());

            Object result = mediaService.uploadVideo(file, title, description, subjectId, classId, academicYearId, user);
            return ResponseEntity.ok(ApiResponse.success("Video uploaded successfully", result));

        } catch (Exception e) {
            log.error("Error uploading video: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to upload video: " + e.getMessage()));
        }
    }
}

