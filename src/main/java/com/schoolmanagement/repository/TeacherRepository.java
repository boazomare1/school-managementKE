package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByTscNumber(String tscNumber);

    List<Teacher> findByDepartment(String department);

    List<Teacher> findByRole(Teacher.TeacherRole role);

    Optional<Teacher> findByAssignedClassId(Long classId);

    Optional<Teacher> findByAssignedDormitoryId(Long dormitoryId);

    List<Teacher> findByIsActiveTrue();

    @Query("SELECT t FROM Teacher t JOIN t.user u JOIN u.roles r WHERE r.name = :role AND t.isActive = true")
    List<Teacher> findByUserRolesAndIsActiveTrue(@Param("role") String role);
    
    Optional<Teacher> findByUserId(Long userId);
}
