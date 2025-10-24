package com.schoolmanagement.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserCreation() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        // When
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        // Then
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertTrue(user.getIsActive());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testUserBuilder() {
        // When
        User builtUser = User.builder()
                .username("builderuser")
                .email("builder@example.com")
                .password("builderpass")
                .isActive(true)
                .build();

        // Then
        assertEquals("builderuser", builtUser.getUsername());
        assertEquals("builder@example.com", builtUser.getEmail());
        assertEquals("builderpass", builtUser.getPassword());
        assertTrue(builtUser.getIsActive());
    }

    @Test
    void testUserEquality() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("sameuser");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("sameuser");

        // When & Then
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getUsername(), user2.getUsername());
    }
}
