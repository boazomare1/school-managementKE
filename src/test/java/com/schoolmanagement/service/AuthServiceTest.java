package com.schoolmanagement.service;

import com.schoolmanagement.dto.JwtResponse;
import com.schoolmanagement.dto.LoginRequest;
import com.schoolmanagement.dto.RegisterRequest;
import com.schoolmanagement.dto.UserResponse;
import com.schoolmanagement.entity.Role;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.repository.RoleRepository;
import com.schoolmanagement.repository.UserRepository;
import com.schoolmanagement.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtils jwtUtils;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private Role testRole;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .id(1L)
                .name(Role.RoleName.STUDENT)
                .description("Student role")
                .build();
        
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .isActive(true)
                .isEmailVerified(false)
                .roles(Set.of(testRole))
                .build();
        
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
    }
    
    @Test
    void register_ShouldReturnUserResponse_WhenValidRequest() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(Role.RoleName.STUDENT)).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        UserResponse result = authService.register(registerRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findByName(Role.RoleName.STUDENT);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void register_ShouldThrowBadRequestException_WhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        
        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> authService.register(registerRequest));
        
        assertEquals("Error: Username is already taken!", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void register_ShouldThrowBadRequestException_WhenEmailExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> authService.register(registerRequest));
        
        assertEquals("Error: Email is already in use!", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void login_ShouldReturnJwtResponse_WhenValidCredentials() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwtToken");
        
        // When
        JwtResponse result = authService.login(loginRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
    }
}



