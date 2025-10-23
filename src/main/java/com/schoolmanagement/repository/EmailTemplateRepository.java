package com.schoolmanagement.repository;

import com.schoolmanagement.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    List<EmailTemplate> findBySchoolIdAndIsActiveTrueOrderByName(Long schoolId);

    Optional<EmailTemplate> findBySchoolIdAndTypeAndIsActiveTrue(Long schoolId, EmailTemplate.EmailType type);

    Optional<EmailTemplate> findBySchoolIdAndNameAndIsActiveTrue(Long schoolId, String name);

    List<EmailTemplate> findByTypeAndIsActiveTrueAndIsDefaultTrue(EmailTemplate.EmailType type);
}


