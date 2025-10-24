package com.schoolmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SchoolManagementSystemApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures the Spring context loads successfully
        // The test passes if the Spring context loads without exceptions
        assert true; // Context loaded successfully
    }

    @Test
    void applicationStarts() {
        // This test verifies the application can start
        assert true; // Application started successfully
    }
}
