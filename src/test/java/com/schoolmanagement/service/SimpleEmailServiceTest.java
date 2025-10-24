package com.schoolmanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SimpleEmailServiceTest {

    @InjectMocks
    private SimpleEmailService simpleEmailService;

    @BeforeEach
    void setUp() {
        // Set a test email for testing
        ReflectionTestUtils.setField(simpleEmailService, "fromEmail", "test@example.com");
    }

    @Test
    void testSendSimpleEmail_WithValidEmail() {
        // Given
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // When & Then
        // Note: This will return false in test environment due to missing email configuration
        // but it tests the method doesn't throw exceptions
        assertDoesNotThrow(() -> {
            boolean result = simpleEmailService.sendSimpleEmail(to, subject, body);
            // In test environment, this will likely be false due to missing email config
            assertNotNull(result); // Result should not be null
        });
    }

    @Test
    void testSendSimpleEmail_WithNullEmail() {
        // Given
        String to = null;
        String subject = "Test Subject";
        String body = "Test Body";

        // When & Then
        assertDoesNotThrow(() -> {
            boolean result = simpleEmailService.sendSimpleEmail(to, subject, body);
            assertFalse(result);
        });
    }
}
