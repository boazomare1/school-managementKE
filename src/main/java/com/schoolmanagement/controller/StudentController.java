package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.UserResponse;
import com.schoolmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {
    
    private final UserService userService;
    
    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllStudents() {
        log.info("Get all students request");
        
        List<UserResponse> students = userService.getAllStudents();
        
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        log.info("Get current user profile request");
        
        UserResponse userResponse = userService.getCurrentUser();
        
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", userResponse));
    }
}



