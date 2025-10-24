package com.schoolmanagement.repository;

import com.schoolmanagement.entity.PayrollDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollDeductionRepository extends JpaRepository<PayrollDeduction, Long> {
    List<PayrollDeduction> findByPayrollId(Long payrollId);
}

