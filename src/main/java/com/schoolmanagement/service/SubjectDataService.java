package com.schoolmanagement.service;

import com.schoolmanagement.entity.Subject;
import com.schoolmanagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectDataService implements CommandLineRunner {

    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (subjectRepository.count() == 0) {
            log.info("Initializing Kenyan education system subjects...");
            initializeSubjects();
            log.info("Subjects initialized successfully!");
        }
    }

    private void initializeSubjects() {
        // 8-4-4 System Subjects
        List<Subject> eightFourFourSubjects = Arrays.asList(
            // Core Subjects
            createSubject("ENG", "English", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "English Language", "Form 1-4", "All"),
            createSubject("KIS", "Kiswahili", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "Kiswahili Language", "Form 1-4", "All"),
            createSubject("MATH", "Mathematics", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "Mathematics", "Form 1-4", "All"),
            createSubject("BIO", "Biology", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "Biology", "Form 1-4", "Science"),
            createSubject("CHEM", "Chemistry", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "Chemistry", "Form 1-4", "Science"),
            createSubject("PHY", "Physics", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "Physics", "Form 1-4", "Science"),
            createSubject("HIST", "History", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "History and Government", "Form 1-4", "All"),
            createSubject("GEO", "Geography", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "Geography", "Form 1-4", "All"),
            createSubject("CRE", "Christian Religious Education", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "CRE", "Form 1-4", "All"),
            createSubject("IRE", "Islamic Religious Education", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "IRE", "Form 1-4", "All"),
            createSubject("HRE", "Hindu Religious Education", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.CORE, "HRE", "Form 1-4", "All"),
            
            // Elective Subjects
            createSubject("FRENCH", "French", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "French Language", "Form 1-4", "All"),
            createSubject("GERMAN", "German", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "German Language", "Form 1-4", "All"),
            createSubject("ARABIC", "Arabic", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "Arabic Language", "Form 1-4", "All"),
            createSubject("MUSIC", "Music", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "Music", "Form 1-4", "All"),
            createSubject("ART", "Art and Design", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "Art and Design", "Form 1-4", "All"),
            createSubject("COMP", "Computer Studies", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "Computer Studies", "Form 1-4", "All"),
            createSubject("BUS", "Business Studies", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "Business Studies", "Form 1-4", "All"),
            createSubject("AGR", "Agriculture", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "Agriculture", "Form 1-4", "All"),
            createSubject("HSC", "Home Science", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.ELECTIVE, "Home Science", "Form 1-4", "All"),
            createSubject("PE", "Physical Education", Subject.CurriculumType.EIGHT_FOUR_FOUR, Subject.SubjectCategory.PHYSICAL, "Physical Education", "Form 1-4", "All")
        );

        // CBC Learning Areas
        List<Subject> cbcSubjects = Arrays.asList(
            // Language Activities
            createCBCSubject("LA_ENG", "English Language Activities", "Language Activities", "Reading", "English Language Activities for CBC"),
            createCBCSubject("LA_KIS", "Kiswahili Language Activities", "Language Activities", "Reading", "Kiswahili Language Activities for CBC"),
            createCBCSubject("LA_IND", "Indigenous Language Activities", "Language Activities", "Reading", "Indigenous Language Activities for CBC"),
            
            // Mathematical Activities
            createCBCSubject("MA_NUM", "Number Work", "Mathematical Activities", "Number Work", "Number Work for CBC"),
            createCBCSubject("MA_MEAS", "Measurement", "Mathematical Activities", "Measurement", "Measurement for CBC"),
            createCBCSubject("MA_GEOM", "Geometry", "Mathematical Activities", "Geometry", "Geometry for CBC"),
            
            // Environmental Activities
            createCBCSubject("EA_SCI", "Science and Technology", "Environmental Activities", "Science", "Science and Technology for CBC"),
            createCBCSubject("EA_SOC", "Social Studies", "Environmental Activities", "Social Studies", "Social Studies for CBC"),
            createCBCSubject("EA_AGR", "Agriculture", "Environmental Activities", "Agriculture", "Agriculture for CBC"),
            
            // Creative Activities
            createCBCSubject("CA_ART", "Art and Craft", "Creative Activities", "Art", "Art and Craft for CBC"),
            createCBCSubject("CA_MUS", "Music", "Creative Activities", "Music", "Music for CBC"),
            createCBCSubject("CA_DRAMA", "Drama", "Creative Activities", "Drama", "Drama for CBC"),
            
            // Religious Activities
            createCBCSubject("RA_CRE", "Christian Religious Education", "Religious Activities", "CRE", "CRE for CBC"),
            createCBCSubject("RA_IRE", "Islamic Religious Education", "Religious Activities", "IRE", "IRE for CBC"),
            createCBCSubject("RA_HRE", "Hindu Religious Education", "Religious Activities", "HRE", "HRE for CBC"),
            
            // Physical and Health Education
            createCBCSubject("PHE_PE", "Physical Education", "Physical and Health Education", "Physical Education", "Physical Education for CBC"),
            createCBCSubject("PHE_HEALTH", "Health Education", "Physical and Health Education", "Health", "Health Education for CBC")
        );

        subjectRepository.saveAll(eightFourFourSubjects);
        subjectRepository.saveAll(cbcSubjects);
    }

    private Subject createSubject(String code, String name, Subject.CurriculumType curriculumType, 
                                Subject.SubjectCategory category, String description, String formLevel, String stream) {
        return Subject.builder()
                .code(code)
                .name(name)
                .curriculumType(curriculumType)
                .category(category)
                .description(description)
                .formLevel(formLevel)
                .stream(stream)
                .isActive(true)
                .build();
    }

    private Subject createCBCSubject(String code, String name, String learningArea, String competency, String description) {
        return Subject.builder()
                .code(code)
                .name(name)
                .curriculumType(Subject.CurriculumType.CBC)
                .category(Subject.SubjectCategory.CORE)
                .description(description)
                .learningArea(learningArea)
                .competency(competency)
                .isActive(true)
                .build();
    }
}

