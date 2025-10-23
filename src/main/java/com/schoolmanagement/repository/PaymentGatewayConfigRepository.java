package com.schoolmanagement.repository;

import com.schoolmanagement.entity.PaymentGatewayConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentGatewayConfigRepository extends JpaRepository<PaymentGatewayConfig, Long> {
    
    List<PaymentGatewayConfig> findBySchoolIdAndIsActiveTrue(Long schoolId);
    
    Optional<PaymentGatewayConfig> findBySchoolIdAndGatewayNameAndIsActiveTrue(Long schoolId, String gatewayName);
    
    List<PaymentGatewayConfig> findByGatewayNameAndIsActiveTrue(String gatewayName);
    
    @Query("SELECT p FROM PaymentGatewayConfig p WHERE p.school.id = :schoolId AND p.gatewayName = :gatewayName AND p.environment = :environment AND p.isActive = true")
    Optional<PaymentGatewayConfig> findBySchoolIdAndGatewayNameAndEnvironmentAndIsActiveTrue(
        @Param("schoolId") Long schoolId, 
        @Param("gatewayName") String gatewayName, 
        @Param("environment") String environment
    );
    
    @Query("SELECT p FROM PaymentGatewayConfig p WHERE p.gatewayName = :gatewayName AND p.environment = :environment AND p.isActive = true")
    List<PaymentGatewayConfig> findByGatewayNameAndEnvironmentAndIsActiveTrue(
        @Param("gatewayName") String gatewayName, 
        @Param("environment") String environment
    );
}

