package com.schoolmanagement.repository;

import com.schoolmanagement.entity.NamingSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NamingSeriesRepository extends JpaRepository<NamingSeries, Long> {
    
    List<NamingSeries> findByIsActiveTrueOrderByNameAsc();
    
    List<NamingSeries> findBySeriesTypeAndIsActiveTrueOrderByNameAsc(NamingSeries.SeriesType seriesType);
    
    Optional<NamingSeries> findByNameAndIsActiveTrue(String name);
    
    Optional<NamingSeries> findBySeriesTypeAndIsDefaultTrueAndIsActiveTrue(NamingSeries.SeriesType seriesType);
    
    @Query("SELECT n FROM NamingSeries n WHERE n.seriesType = :seriesType AND n.isActive = true ORDER BY n.isDefault DESC, n.name ASC")
    List<NamingSeries> findBySeriesTypeOrderByDefaultAndName(@Param("seriesType") NamingSeries.SeriesType seriesType);
    
    @Query("SELECT n FROM NamingSeries n WHERE n.isActive = true AND " +
           "(LOWER(n.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(n.prefix) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<NamingSeries> findBySearchTerm(@Param("search") String search);
    
    @Query("SELECT COUNT(n) FROM NamingSeries n WHERE n.seriesType = :seriesType AND n.isActive = true")
    Long countBySeriesType(@Param("seriesType") NamingSeries.SeriesType seriesType);
}
