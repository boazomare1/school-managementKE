package com.schoolmanagement.dto;

import com.schoolmanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of biometric dining access authentication
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiningAccessResult {
    
    private boolean accessGranted;
    private String message;
    private String ticketNumber;
    private String ticketContent;
    private User student;
    private String timestamp;
    
    /**
     * Create a successful access result with thermal ticket
     */
    public static DiningAccessResult granted(String ticketNumber, String ticketContent, User student) {
        return DiningAccessResult.builder()
            .accessGranted(true)
            .message("Access granted - Fee payment verified")
            .ticketNumber(ticketNumber)
            .ticketContent(ticketContent)
            .student(student)
            .timestamp(java.time.LocalDateTime.now().toString())
            .build();
    }
    
    /**
     * Create a denied access result
     */
    public static DiningAccessResult denied(String reason) {
        return DiningAccessResult.builder()
            .accessGranted(false)
            .message("Access denied: " + reason)
            .ticketNumber(null)
            .ticketContent(null)
            .student(null)
            .timestamp(java.time.LocalDateTime.now().toString())
            .build();
    }
}

