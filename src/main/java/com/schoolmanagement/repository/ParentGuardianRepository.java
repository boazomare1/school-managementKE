package com.schoolmanagement.repository;

import com.schoolmanagement.entity.ParentGuardian;
import com.schoolmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentGuardianRepository extends JpaRepository<ParentGuardian, Long> {

    /**
     * Find all parent/guardian relationships for a specific student
     */
    List<ParentGuardian> findByStudentIdAndIsActiveTrue(Long studentId);

    /**
     * Find all students for a specific parent/guardian
     */
    List<ParentGuardian> findByParentIdAndIsActiveTrue(Long parentId);

    /**
     * Find primary contact for a student
     */
    Optional<ParentGuardian> findByStudentIdAndIsPrimaryContactTrueAndIsActiveTrue(Long studentId);

    /**
     * Find emergency contacts for a student
     */
    List<ParentGuardian> findByStudentIdAndIsEmergencyContactTrueAndIsActiveTrue(Long studentId);

    /**
     * Find parents/guardians who can receive notifications for a student
     */
    List<ParentGuardian> findByStudentIdAndCanReceiveNotificationsTrueAndIsActiveTrue(Long studentId);

    /**
     * Find parents/guardians who can pickup a student
     */
    List<ParentGuardian> findByStudentIdAndCanPickupStudentTrueAndIsActiveTrue(Long studentId);

    /**
     * Check if a parent/guardian relationship already exists
     */
    boolean existsByStudentIdAndParentIdAndIsActiveTrue(Long studentId, Long parentId);

    /**
     * Count active parent/guardian relationships for a student
     */
    long countByStudentIdAndIsActiveTrue(Long studentId);

    /**
     * Find by relationship type and student
     */
    List<ParentGuardian> findByStudentIdAndRelationshipTypeAndIsActiveTrue(
        Long studentId, ParentGuardian.RelationshipType relationshipType);

    /**
     * Find by priority type and student
     */
    List<ParentGuardian> findByStudentIdAndPriorityTypeAndIsActiveTrue(
        Long studentId, ParentGuardian.PriorityType priorityType);

    /**
     * Custom query to find students with maximum parent/guardian relationships
     */
    @Query("SELECT pg.student FROM ParentGuardian pg WHERE pg.isActive = true " +
           "GROUP BY pg.student HAVING COUNT(pg) >= :maxRelationships")
    List<User> findStudentsWithMaxRelationships(@Param("maxRelationships") long maxRelationships);

    /**
     * Find all parent/guardian relationships for multiple students
     */
    @Query("SELECT pg FROM ParentGuardian pg WHERE pg.student.id IN :studentIds AND pg.isActive = true")
    List<ParentGuardian> findByStudentIdInAndIsActiveTrue(@Param("studentIds") List<Long> studentIds);
}

