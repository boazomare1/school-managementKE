package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.UserResponse;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {
        log.info("Get all users request - page: {}, size: {}, role: {}, search: {}", page, size, role, search);
        
        Page<UserResponse> users = userService.getAllUsers(page, size, role, search);
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("Get user by ID request: {}", id);
        
        UserResponse user = userService.getUserById(id);
        
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        log.info("Update user request for ID: {}", id);
        
        UserResponse updatedUser = userService.updateUser(id, user);
        
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Delete user request for ID: {}", id);
        
        userService.deleteUser(id);
        
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
    
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable String role) {
        log.info("Get users by role request: {}", role);
        
        List<UserResponse> users = userService.getUsersByRole(role);
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getActiveUsers() {
        log.info("Get active users request");
        
        List<UserResponse> users = userService.getActiveUsers();
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable Long id) {
        log.info("Activate user request for ID: {}", id);
        
        UserResponse user = userService.activateUser(id);
        
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", user));
    }
    
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Long id) {
        log.info("Deactivate user request for ID: {}", id);
        
        UserResponse user = userService.deactivateUser(id);
        
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", user));
    }
}

