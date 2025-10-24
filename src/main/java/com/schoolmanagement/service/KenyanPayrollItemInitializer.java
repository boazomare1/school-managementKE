package com.schoolmanagement.service;

import com.schoolmanagement.entity.PayrollItem;
import com.schoolmanagement.repository.PayrollItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class KenyanPayrollItemInitializer implements CommandLineRunner {
    
    private final PayrollItemRepository payrollItemRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeKenyanPayrollItems();
    }
    
    private void initializeKenyanPayrollItems() {
        try {
            log.info("Initializing Kenyan payroll items...");
            
            // Check if items already exist
            if (payrollItemRepository.count() > 0) {
                log.info("Payroll items already exist, skipping initialization");
                return;
            }
            
            // ALLOWANCES
            createAllowance("House Allowance", "HOUSE_ALLOW", false, BigDecimal.valueOf(15000), "Housing allowance for employees");
            createAllowance("Medical Allowance", "MEDICAL_ALLOW", false, BigDecimal.valueOf(10000), "Medical allowance for employees");
            createAllowance("Transport Allowance", "TRANSPORT_ALLOW", false, BigDecimal.valueOf(8000), "Transport allowance for employees");
            createAllowance("Lunch Allowance", "LUNCH_ALLOW", false, BigDecimal.valueOf(5000), "Lunch allowance for employees");
            createAllowance("Communication Allowance", "COMM_ALLOW", false, BigDecimal.valueOf(3000), "Communication allowance for employees");
            
            // STATUTORY DEDUCTIONS
            createDeduction("PAYE", "PAYE", true, true, BigDecimal.valueOf(10.0), BigDecimal.valueOf(0), BigDecimal.valueOf(1000000), "Pay As You Earn tax", "STATUTORY", "PAYE");
            createDeduction("NSSF", "NSSF", true, false, BigDecimal.valueOf(200), BigDecimal.valueOf(200), BigDecimal.valueOf(200), "National Social Security Fund", "STATUTORY", "NSSF");
            createDeduction("NHIF", "NHIF", true, false, BigDecimal.valueOf(500), BigDecimal.valueOf(150), BigDecimal.valueOf(1700), "National Hospital Insurance Fund", "STATUTORY", "NHIF");
            
            // NEW KENYAN DEDUCTIONS (2024)
            createDeduction("Housing Levy", "HOUSING_LEVY", true, true, BigDecimal.valueOf(1.5), BigDecimal.valueOf(0), BigDecimal.valueOf(1000000), "Affordable Housing Levy", "STATUTORY", "HOUSING_LEVY");
            createDeduction("HELB", "HELB", false, false, BigDecimal.valueOf(1000), BigDecimal.valueOf(0), BigDecimal.valueOf(5000), "Higher Education Loans Board", "LOANS", "HELB");
            
            // OPTIONAL DEDUCTIONS
            createDeduction("Pension Contribution", "PENSION", false, false, BigDecimal.valueOf(2000), BigDecimal.valueOf(0), BigDecimal.valueOf(10000), "Voluntary pension contribution", "BENEFITS", "PENSION");
            createDeduction("Insurance Premium", "INSURANCE", false, false, BigDecimal.valueOf(1500), BigDecimal.valueOf(0), BigDecimal.valueOf(5000), "Group insurance premium", "BENEFITS", "INSURANCE");
            
            log.info("Successfully initialized Kenyan payroll items");
            
        } catch (Exception e) {
            log.error("Error initializing Kenyan payroll items: {}", e.getMessage());
        }
    }
    
    private void createAllowance(String name, String code, boolean isMandatory, BigDecimal fixedAmount, String description) {
        PayrollItem allowance = PayrollItem.builder()
                .name(name)
                .code(code)
                .type(PayrollItem.PayrollItemType.ALLOWANCE)
                .isActive(true)
                .isMandatory(isMandatory)
                .isPercentage(false)
                .fixedAmount(fixedAmount)
                .isTaxable(true)
                .description(description)
                .category("BENEFITS")
                .build();
        
        payrollItemRepository.save(allowance);
        log.info("Created allowance: {}", name);
    }
    
    private void createDeduction(String name, String code, boolean isMandatory, boolean isPercentage, 
                                BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount, 
                                String description, String category, String governmentCode) {
        PayrollItem deduction = PayrollItem.builder()
                .name(name)
                .code(code)
                .type(PayrollItem.PayrollItemType.DEDUCTION)
                .isActive(true)
                .isMandatory(isMandatory)
                .isPercentage(isPercentage)
                .fixedAmount(isPercentage ? null : amount)
                .percentageRate(isPercentage ? amount : null)
                .minimumAmount(minAmount)
                .maximumAmount(maxAmount)
                .isTaxable(false)
                .description(description)
                .category(category)
                .governmentCode(governmentCode)
                .build();
        
        payrollItemRepository.save(deduction);
        log.info("Created deduction: {}", name);
    }
}
