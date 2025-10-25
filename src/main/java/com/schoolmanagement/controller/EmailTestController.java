package com.schoolmanagement.controller;

import com.schoolmanagement.service.SimpleEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email/test")
@RequiredArgsConstructor
@Slf4j
public class EmailTestController {

    // Constants to avoid literal duplication
    private static final String SUCCESS_KEY = "success";
    private static final String MESSAGE_KEY = "message";
    private static final String SUCCESS_TRUE = "true";
    private static final String SUCCESS_FALSE = "false";
    
    private final SimpleEmailService emailService;

    /**
     * Send test email
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendTestEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message) {
        try {
            log.info("Sending test email to: {}", to);
            
            boolean success = emailService.sendSimpleEmail(to, subject, message);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    SUCCESS_KEY, SUCCESS_TRUE,
                    MESSAGE_KEY, "Test email sent successfully to " + to
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    SUCCESS_KEY, SUCCESS_FALSE,
                    MESSAGE_KEY, "Failed to send test email to " + to
                ));
            }
        } catch (Exception e) {
            log.error("Error sending test email: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                SUCCESS_KEY, SUCCESS_FALSE,
                MESSAGE_KEY, "Error sending test email: " + e.getMessage()
            ));
        }
    }

    /**
     * Send HTML test email
     */
    @PostMapping("/send-html")
    public ResponseEntity<Map<String, String>> sendHtmlTestEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String htmlMessage) {
        try {
            log.info("Sending HTML test email to: {}", to);
            
            boolean success = emailService.sendHtmlEmail(to, subject, htmlMessage);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    SUCCESS_KEY, SUCCESS_TRUE,
                    MESSAGE_KEY, "HTML test email sent successfully to " + to
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    SUCCESS_KEY, SUCCESS_FALSE,
                    MESSAGE_KEY, "Failed to send HTML test email to " + to
                ));
            }
        } catch (Exception e) {
            log.error("Error sending HTML test email: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                SUCCESS_KEY, SUCCESS_FALSE,
                MESSAGE_KEY, "Error sending HTML test email: " + e.getMessage()
            ));
        }
    }

    /**
     * Send welcome email
     */
    @PostMapping("/welcome")
    public ResponseEntity<Map<String, String>> sendWelcomeEmail(
            @RequestParam String to,
            @RequestParam String name,
            @RequestParam String role) {
        try {
            log.info("Sending welcome email to: {}", to);
            
            boolean success = emailService.sendWelcomeEmail(to, name, role);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    SUCCESS_KEY, SUCCESS_TRUE,
                    MESSAGE_KEY, "Welcome email sent successfully to " + to
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    SUCCESS_KEY, SUCCESS_FALSE,
                    MESSAGE_KEY, "Failed to send welcome email to " + to
                ));
            }
        } catch (Exception e) {
            log.error("Error sending welcome email: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                SUCCESS_KEY, SUCCESS_FALSE,
                MESSAGE_KEY, "Error sending welcome email: " + e.getMessage()
            ));
        }
    }
}
