package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.JwtResponse;
import com.schoolmanagement.dto.LoginRequest;
import com.schoolmanagement.dto.RegisterRequest;
import com.schoolmanagement.dto.UserResponse;
import com.schoolmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register request for user: {}", registerRequest.getUsername());
        
        UserResponse userResponse = authService.register(registerRequest);
        
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", userResponse));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request for user: {}", loginRequest.getUsernameOrEmail());
        
        JwtResponse jwtResponse = authService.login(loginRequest);
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refreshToken(@RequestParam String refreshToken) {
        log.info("Refresh token request");
        
        JwtResponse jwtResponse = authService.refreshToken(refreshToken);
        
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", jwtResponse));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestParam String refreshToken) {
        log.info("Logout request");
        
        authService.logout(refreshToken);
        
        return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out successfully"));
    }
}



