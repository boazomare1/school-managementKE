package com.schoolmanagement.config;

import com.schoolmanagement.entity.Role;
import com.schoolmanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }
    
    private void initializeRoles() {
        for (Role.RoleName roleName : Role.RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .description("Role for " + roleName.name().toLowerCase())
                        .build();
                
                roleRepository.save(role);
                log.info("Created role: {}", roleName);
            }
        }
    }
}



