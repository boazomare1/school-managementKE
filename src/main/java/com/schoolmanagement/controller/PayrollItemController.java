package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.PayrollItemDto;
import com.schoolmanagement.entity.PayrollItem;
import com.schoolmanagement.service.PayrollItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll-items")
@RequiredArgsConstructor
@Slf4j
public class PayrollItemController {
    
    private final PayrollItemService payrollItemService;
    
    // Create a new payroll item
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PayrollItemDto>> createPayrollItem(@Valid @RequestBody PayrollItemDto payrollItemDto) {
        log.info("Creating payroll item: {}", payrollItemDto.getName());
        return ResponseEntity.ok(payrollItemService.createPayrollItem(payrollItemDto));
    }
    
    // Get all payroll items
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<PayrollItemDto>>> getAllPayrollItems() {
        log.info("Fetching all payroll items");
        return ResponseEntity.ok(payrollItemService.getAllPayrollItems());
    }
    
    // Get payroll items by type
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<PayrollItemDto>>> getPayrollItemsByType(@PathVariable PayrollItem.PayrollItemType type) {
        log.info("Fetching payroll items by type: {}", type);
        return ResponseEntity.ok(payrollItemService.getPayrollItemsByType(type));
    }
    
    // Get mandatory payroll items
    @GetMapping("/mandatory")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<PayrollItemDto>>> getMandatoryPayrollItems() {
        log.info("Fetching mandatory payroll items");
        return ResponseEntity.ok(payrollItemService.getMandatoryPayrollItems());
    }
    
    // Update payroll item
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PayrollItemDto>> updatePayrollItem(
            @PathVariable Long id, 
            @Valid @RequestBody PayrollItemDto payrollItemDto) {
        log.info("Updating payroll item with ID: {}", id);
        return ResponseEntity.ok(payrollItemService.updatePayrollItem(id, payrollItemDto));
    }
    
    // Deactivate payroll item
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<Void>> deactivatePayrollItem(@PathVariable Long id) {
        log.info("Deactivating payroll item with ID: {}", id);
        return ResponseEntity.ok(payrollItemService.deactivatePayrollItem(id));
    }
    
    // Search payroll items
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<PayrollItemDto>>> searchPayrollItems(@RequestParam String q) {
        log.info("Searching payroll items with term: {}", q);
        return ResponseEntity.ok(payrollItemService.searchPayrollItems(q));
    }
}
