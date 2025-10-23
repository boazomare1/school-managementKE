package com.schoolmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolmanagement.dto.JwtResponse;
import com.schoolmanagement.dto.LoginRequest;
import com.schoolmanagement.dto.RegisterRequest;
import com.schoolmanagement.dto.UserResponse;
import com.schoolmanagement.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserResponse userResponse;
    private JwtResponse jwtResponse;
    
    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .role("STUDENT")
                .build();
        
        loginRequest = LoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("password123")
                .build();
        
        userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(Set.of("STUDENT"))
                .build();
        
        jwtResponse = JwtResponse.builder()
                .token("jwtToken")
                .refreshToken("refreshToken")
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(Set.of("STUDENT"))
                .build();
    }
    
    @Test
    void register_ShouldReturnUserResponse_WhenValidRequest() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(userResponse);
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }
    
    @Test
    void register_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .username("") // Invalid: empty username
                .email("invalid-email") // Invalid: malformed email
                .password("123") // Invalid: too short password
                .firstName("") // Invalid: empty first name
                .lastName("") // Invalid: empty last name
                .role("") // Invalid: empty role
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void login_ShouldReturnJwtResponse_WhenValidCredentials() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(jwtResponse);
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value("jwtToken"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
    
    @Test
    void login_ShouldReturnBadRequest_WhenInvalidCredentials() throws Exception {
        // Given
        LoginRequest invalidRequest = LoginRequest.builder()
                .usernameOrEmail("") // Invalid: empty username
                .password("") // Invalid: empty password
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}



