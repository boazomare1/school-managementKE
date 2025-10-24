package com.schoolmanagement.repository;

import com.schoolmanagement.entity.SupportStaff;
import com.schoolmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupportStaffRepository extends JpaRepository<SupportStaff, Long> {
    
    Optional<SupportStaff> findByUser(User user);
    
    Optional<SupportStaff> findByEmployeeId(String employeeId);
    
    List<SupportStaff> findByIsActiveTrue();
    
    Page<SupportStaff> findByIsActiveTrue(Pageable pageable);
    
    List<SupportStaff> findByStaffTypeAndIsActiveTrue(SupportStaff.SupportStaffType staffType);
    
    List<SupportStaff> findByEmploymentStatusAndIsActiveTrue(SupportStaff.EmploymentStatus employmentStatus);
    
    Page<SupportStaff> findByEmploymentStatusAndIsActiveTrueOrderByCreatedAtDesc(SupportStaff.EmploymentStatus employmentStatus, Pageable pageable);
    
    Page<SupportStaff> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    List<SupportStaff> findByDepartmentAndIsActiveTrue(String department);
    
    List<SupportStaff> findByHireDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM SupportStaff s WHERE s.isActive = true AND " +
           "(LOWER(s.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.position) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SupportStaff> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT s FROM SupportStaff s WHERE s.isActive = true AND s.employmentStatus = :status AND " +
           "(LOWER(s.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.employeeId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SupportStaff> findByStatusAndSearchTerm(@Param("status") SupportStaff.EmploymentStatus status, 
                                                @Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(s) FROM SupportStaff s WHERE s.isActive = true AND s.employmentStatus = :status")
    Long countByEmploymentStatus(@Param("status") SupportStaff.EmploymentStatus status);
    
    @Query("SELECT s FROM SupportStaff s WHERE s.isActive = true AND s.terminationDate IS NULL AND " +
           "s.employmentStatus IN ('ACTIVE', 'ON_LEAVE')")
    List<SupportStaff> findActiveStaff();
}
