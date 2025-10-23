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
                    "success", "true",
                    "message", "Test email sent successfully to " + to
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", "false",
                    "message", "Failed to send test email to " + to
                ));
            }
        } catch (Exception e) {
            log.error("Error sending test email: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", "false",
                "message", "Error sending test email: " + e.getMessage()
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
                    "success", "true",
                    "message", "HTML test email sent successfully to " + to
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", "false",
                    "message", "Failed to send HTML test email to " + to
                ));
            }
        } catch (Exception e) {
            log.error("Error sending HTML test email: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", "false",
                "message", "Error sending HTML test email: " + e.getMessage()
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
                    "success", "true",
                    "message", "Welcome email sent successfully to " + to
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", "false",
                    "message", "Failed to send welcome email to " + to
                ));
            }
        } catch (Exception e) {
            log.error("Error sending welcome email: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", "false",
                "message", "Error sending welcome email: " + e.getMessage()
            ));
        }
    }
}
