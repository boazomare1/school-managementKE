package com.schoolmanagement.repository;

import com.schoolmanagement.entity.PayrollItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollItemRepository extends JpaRepository<PayrollItem, Long> {
    
    List<PayrollItem> findByIsActiveTrueOrderByNameAsc();
    
    List<PayrollItem> findByTypeAndIsActiveTrueOrderByNameAsc(PayrollItem.PayrollItemType type);
    
    List<PayrollItem> findByIsMandatoryTrueAndIsActiveTrueOrderByNameAsc();
    
    List<PayrollItem> findByCategoryAndIsActiveTrueOrderByNameAsc(String category);
    
    Optional<PayrollItem> findByCodeAndIsActiveTrue(String code);
    
    @Query("SELECT p FROM PayrollItem p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<PayrollItem> findBySearchTerm(@Param("search") String search);
    
    @Query("SELECT p FROM PayrollItem p WHERE p.isActive = true AND p.type = :type AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<PayrollItem> findByTypeAndSearchTerm(@Param("type") PayrollItem.PayrollItemType type, 
                                             @Param("search") String search);
}
