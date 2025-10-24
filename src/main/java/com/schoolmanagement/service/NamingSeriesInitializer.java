package com.schoolmanagement.service;

import com.schoolmanagement.entity.NamingSeries;
import com.schoolmanagement.repository.NamingSeriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NamingSeriesInitializer implements CommandLineRunner {
    
    private final NamingSeriesRepository namingSeriesRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeDefaultNamingSeries();
    }
    
    private void initializeDefaultNamingSeries() {
        try {
            log.info("Initializing default naming series...");
            
            // Check if series already exist
            if (namingSeriesRepository.count() > 0) {
                log.info("Naming series already exist, skipping initialization");
                return;
            }
            
            // Employee ID Series
            createNamingSeries(
                "EMP", 
                "Employee ID Series", 
                "EMP", 
                "001", 
                1, 
                1, 
                3, 
                "-", 
                NamingSeries.SeriesType.EMPLOYEE, 
                "Default employee ID series (EMP-001, EMP-002, etc.)",
                true
            );
            
            // Student Admission Number Series
            createNamingSeries(
                "STU", 
                "Student Admission Number Series", 
                "STU", 
                "2024", 
                1, 
                1, 
                4, 
                "-", 
                NamingSeries.SeriesType.STUDENT, 
                "Default student admission number series (STU-2024-0001, STU-2024-0002, etc.)",
                true
            );
            
            // Teacher ID Series
            createNamingSeries(
                "TCH", 
                "Teacher ID Series", 
                "TCH", 
                "001", 
                1, 
                1, 
                3, 
                "-", 
                NamingSeries.SeriesType.TEACHER, 
                "Default teacher ID series (TCH-001, TCH-002, etc.)",
                true
            );
            
            // Support Staff ID Series
            createNamingSeries(
                "SUP", 
                "Support Staff ID Series", 
                "SUP", 
                "001", 
                1, 
                1, 
                3, 
                "-", 
                NamingSeries.SeriesType.SUPPORT_STAFF, 
                "Default support staff ID series (SUP-001, SUP-002, etc.)",
                true
            );
            
            // Parent ID Series
            createNamingSeries(
                "PAR", 
                "Parent ID Series", 
                "PAR", 
                "001", 
                1, 
                1, 
                3, 
                "-", 
                NamingSeries.SeriesType.PARENT, 
                "Default parent ID series (PAR-001, PAR-002, etc.)",
                true
            );
            
            // Alternative Employee Series (for different departments)
            createNamingSeries(
                "CHEF", 
                "Chef ID Series", 
                "CHEF", 
                "001", 
                1, 
                1, 
                3, 
                "-", 
                NamingSeries.SeriesType.SUPPORT_STAFF, 
                "Chef ID series (CHEF-001, CHEF-002, etc.)",
                false
            );
            
            // Alternative Student Series (for different years)
            createNamingSeries(
                "STU2025", 
                "Student Admission Number Series 2025", 
                "STU", 
                "2025", 
                1, 
                1, 
                4, 
                "-", 
                NamingSeries.SeriesType.STUDENT, 
                "Student admission number series for 2025 (STU-2025-0001, STU-2025-0002, etc.)",
                false
            );
            
            // Alternative Teacher Series (for different subjects)
            createNamingSeries(
                "MATH", 
                "Mathematics Teacher ID Series", 
                "MATH", 
                "001", 
                1, 
                1, 
                3, 
                "-", 
                NamingSeries.SeriesType.TEACHER, 
                "Mathematics teacher ID series (MATH-001, MATH-002, etc.)",
                false
            );
            
            log.info("Successfully initialized default naming series");
            
        } catch (Exception e) {
            log.error("Error initializing default naming series: {}", e.getMessage());
        }
    }
    
    private void createNamingSeries(String name, String description, String prefix, String suffix, 
                                   Integer startNumber, Integer currentNumber, Integer padding, 
                                   String separator, NamingSeries.SeriesType seriesType, 
                                   String notes, Boolean isDefault) {
        NamingSeries series = NamingSeries.builder()
                .name(name)
                .description(description)
                .prefix(prefix)
                .suffix(suffix)
                .startNumber(startNumber)
                .currentNumber(currentNumber)
                .padding(padding)
                .separator(separator)
                .isActive(true)
                .isDefault(isDefault)
                .seriesType(seriesType)
                .notes(notes)
                .build();
        
        namingSeriesRepository.save(series);
        log.info("Created naming series: {}", name);
    }
}
