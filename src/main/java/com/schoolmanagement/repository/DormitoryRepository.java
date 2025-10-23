package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DormitoryRepository extends JpaRepository<Dormitory, Long> {

    Optional<Dormitory> findByName(String name);

    List<Dormitory> findByIsActiveTrue();

    Optional<Dormitory> findByDormMasterId(Long dormMasterId);

    List<Dormitory> findByCapacityGreaterThan(Integer capacity);
}

