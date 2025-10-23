package com.schoolmanagement.repository;

import com.schoolmanagement.entity.DashboardWidget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardWidgetRepository extends JpaRepository<DashboardWidget, Long> {
    
    List<DashboardWidget> findBySchoolIdAndIsActiveTrue(Long schoolId);
    
    List<DashboardWidget> findBySchoolIdAndWidgetTypeAndIsActiveTrue(Long schoolId, String widgetType);
    
    @Query("SELECT dw FROM DashboardWidget dw WHERE dw.school.id = :schoolId AND dw.isActive = true ORDER BY dw.positionY, dw.positionX")
    List<DashboardWidget> findActiveWidgetsBySchoolOrdered(Long schoolId);
}


