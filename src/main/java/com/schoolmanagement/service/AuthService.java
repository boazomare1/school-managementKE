package com.schoolmanagement.service;

import com.schoolmanagement.dto.JwtResponse;
import com.schoolmanagement.dto.LoginRequest;
import com.schoolmanagement.dto.RegisterRequest;
import com.schoolmanagement.dto.UserResponse;
import com.schoolmanagement.entity.RefreshToken;
import com.schoolmanagement.entity.Role;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.RefreshTokenRepository;
import com.schoolmanagement.repository.RoleRepository;
import com.schoolmanagement.repository.UserRepository;
import com.schoolmanagement.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Error: Username is already taken!");
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }
        
        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .address(registerRequest.getAddress())
                .isActive(true)
                .isEmailVerified(false)
                .build();
        
        // Parse date of birth if provided
        if (registerRequest.getDateOfBirth() != null && !registerRequest.getDateOfBirth().isEmpty()) {
            try {
                user.setDateOfBirth(LocalDateTime.parse(registerRequest.getDateOfBirth()));
            } catch (Exception e) {
                log.warn("Invalid date format for dateOfBirth: {}", registerRequest.getDateOfBirth());
            }
        }
        
        // Set roles
        Set<Role> roles = new HashSet<>();
        String roleName = registerRequest.getRole().toUpperCase();
        
        try {
            Role.RoleName roleEnum = Role.RoleName.valueOf(roleName);
            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
            roles.add(role);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + roleName);
        }
        
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());
        
        return convertToUserResponse(savedUser);
    }
    
    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), 
                                                      loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        User user = (User) authentication.getPrincipal();
        String refreshToken = createRefreshToken(user);
        
        Set<String> roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .collect(java.util.stream.Collectors.toSet());
        
        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .build();
    }
    
    @Transactional
    public JwtResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        
        if (token.getIsRevoked()) {
            throw new BadRequestException("Refresh token has been revoked");
        }
        
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token has expired");
        }
        
        User user = token.getUser();
        String newJwt = jwtUtils.generateJwtTokenFromUsername(user.getUsername());
        String newRefreshToken = createRefreshToken(user);
        
        // Revoke old refresh token
        refreshTokenRepository.revokeToken(refreshToken);
        
        Set<String> roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .collect(java.util.stream.Collectors.toSet());
        
        return JwtResponse.builder()
                .token(newJwt)
                .refreshToken(newRefreshToken)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .build();
    }
    
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.revokeToken(refreshToken);
    }
    
    private String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7); // 7 days
        
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .isRevoked(false)
                .build();
        
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    
    private UserResponse convertToUserResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.toSet());
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(roles)
                .build();
    }
}



