package com.schoolmanagement.repository;

import com.schoolmanagement.entity.PayrollAllowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollAllowanceRepository extends JpaRepository<PayrollAllowance, Long> {
    List<PayrollAllowance> findByPayrollId(Long payrollId);
}

