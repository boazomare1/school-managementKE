package com.schoolmanagement.repository;

import com.schoolmanagement.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    
    Optional<School> findByName(String name);
    
    List<School> findByIsActiveTrue();
    
    @Query("SELECT s FROM School s WHERE s.isActive = true ORDER BY s.name")
    List<School> findAllActiveSchools();
}


