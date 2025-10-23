package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    List<Staff> findByIsActiveTrueOrderByUserFirstNameAsc();
    
    List<Staff> findByStaffTypeAndIsActiveTrueOrderByUserFirstNameAsc(Staff.StaffType staffType);
    
    List<Staff> findByEmploymentTypeAndIsActiveTrueOrderByUserFirstNameAsc(Staff.EmploymentType employmentType);
    
    Optional<Staff> findByEmployeeNumberAndIsActiveTrue(String employeeNumber);
    
    Optional<Staff> findByTscNumberAndIsActiveTrue(String tscNumber);
    
    @Query("SELECT s FROM Staff s WHERE s.user.id = :userId AND s.isActive = true")
    Optional<Staff> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Staff s WHERE s.staffType = :staffType AND s.employmentType = :employmentType AND s.isActive = true ORDER BY s.user.firstName")
    List<Staff> findByStaffTypeAndEmploymentTypeAndIsActiveTrueOrderByUserFirstName(
        @Param("staffType") Staff.StaffType staffType, 
        @Param("employmentType") Staff.EmploymentType employmentType
    );
    
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.staffType = :staffType AND s.isActive = true")
    Long countByStaffTypeAndIsActiveTrue(@Param("staffType") Staff.StaffType staffType);
    
    @Query("SELECT s FROM Staff s WHERE s.basicSalary >= :minSalary AND s.isActive = true ORDER BY s.basicSalary DESC")
    List<Staff> findByBasicSalaryGreaterThanEqualAndIsActiveTrueOrderByBasicSalaryDesc(@Param("minSalary") Double minSalary);
}

