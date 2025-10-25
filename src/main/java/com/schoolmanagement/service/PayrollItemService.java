package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.PayrollItemDto;
import com.schoolmanagement.entity.PayrollItem;
import com.schoolmanagement.repository.PayrollItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PayrollItemService {
    
    private final PayrollItemRepository payrollItemRepository;
    
    // Create a new payroll item
    public ApiResponse<PayrollItemDto> createPayrollItem(PayrollItemDto payrollItemDto) {
        try {
            log.info("Creating payroll item: {}", payrollItemDto.getName());
            
            // Check if code already exists
            if (payrollItemRepository.findByCodeAndIsActiveTrue(payrollItemDto.getCode()).isPresent()) {
                return ApiResponse.error("Payroll item with code '" + payrollItemDto.getCode() + "' already exists");
            }
            
            PayrollItem payrollItem = PayrollItem.builder()
                    .name(payrollItemDto.getName())
                    .code(payrollItemDto.getCode())
                    .type(payrollItemDto.getType())
                    .isActive(payrollItemDto.getIsActive())
                    .isMandatory(payrollItemDto.getIsMandatory())
                    .isPercentage(payrollItemDto.getIsPercentage())
                    .fixedAmount(payrollItemDto.getFixedAmount())
                    .percentageRate(payrollItemDto.getPercentageRate())
                    .minimumAmount(payrollItemDto.getMinimumAmount())
                    .maximumAmount(payrollItemDto.getMaximumAmount())
                    .isTaxable(payrollItemDto.getIsTaxable())
                    .description(payrollItemDto.getDescription())
                    .category(payrollItemDto.getCategory())
                    .governmentCode(payrollItemDto.getGovernmentCode())
                    .build();
            
            PayrollItem savedItem = payrollItemRepository.save(payrollItem);
            log.info("Successfully created payroll item with ID: {}", savedItem.getId());
            
            return ApiResponse.success("Payroll item created successfully", convertToDto(savedItem));
            
        } catch (Exception e) {
            log.error("Error creating payroll item: {}", e.getMessage());
            return ApiResponse.error("Failed to create payroll item: " + e.getMessage());
        }
    }
    
    // Get all payroll items
    public ApiResponse<List<PayrollItemDto>> getAllPayrollItems() {
        try {
            log.info("Fetching all payroll items");
            List<PayrollItem> items = payrollItemRepository.findByIsActiveTrueOrderByNameAsc();
            List<PayrollItemDto> dtoList = items.stream().map(this::convertToDto).toList();
            return ApiResponse.success("Payroll items retrieved successfully", dtoList);
        } catch (Exception e) {
            log.error("Error fetching payroll items: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch payroll items: " + e.getMessage());
        }
    }
    
    // Get payroll items by type
    public ApiResponse<List<PayrollItemDto>> getPayrollItemsByType(PayrollItem.PayrollItemType type) {
        try {
            log.info("Fetching payroll items by type: {}", type);
            List<PayrollItem> items = payrollItemRepository.findByTypeAndIsActiveTrueOrderByNameAsc(type);
            List<PayrollItemDto> dtoList = items.stream().map(this::convertToDto).toList();
            return ApiResponse.success("Payroll items retrieved successfully", dtoList);
        } catch (Exception e) {
            log.error("Error fetching payroll items by type: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch payroll items: " + e.getMessage());
        }
    }
    
    // Get mandatory payroll items
    public ApiResponse<List<PayrollItemDto>> getMandatoryPayrollItems() {
        try {
            log.info("Fetching mandatory payroll items");
            List<PayrollItem> items = payrollItemRepository.findByIsMandatoryTrueAndIsActiveTrueOrderByNameAsc();
            List<PayrollItemDto> dtoList = items.stream().map(this::convertToDto).toList();
            return ApiResponse.success("Mandatory payroll items retrieved successfully", dtoList);
        } catch (Exception e) {
            log.error("Error fetching mandatory payroll items: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch mandatory payroll items: " + e.getMessage());
        }
    }
    
    // Update payroll item
    public ApiResponse<PayrollItemDto> updatePayrollItem(Long id, PayrollItemDto payrollItemDto) {
        try {
            log.info("Updating payroll item with ID: {}", id);
            PayrollItem existingItem = payrollItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payroll item not found"));
            
            // Check if code is being changed and if new code already exists
            if (!existingItem.getCode().equals(payrollItemDto.getCode())) {
                if (payrollItemRepository.findByCodeAndIsActiveTrue(payrollItemDto.getCode()).isPresent()) {
                    return ApiResponse.error("Payroll item with code '" + payrollItemDto.getCode() + "' already exists");
                }
            }
            
            existingItem.setName(payrollItemDto.getName());
            existingItem.setCode(payrollItemDto.getCode());
            existingItem.setType(payrollItemDto.getType());
            existingItem.setIsActive(payrollItemDto.getIsActive());
            existingItem.setIsMandatory(payrollItemDto.getIsMandatory());
            existingItem.setIsPercentage(payrollItemDto.getIsPercentage());
            existingItem.setFixedAmount(payrollItemDto.getFixedAmount());
            existingItem.setPercentageRate(payrollItemDto.getPercentageRate());
            existingItem.setMinimumAmount(payrollItemDto.getMinimumAmount());
            existingItem.setMaximumAmount(payrollItemDto.getMaximumAmount());
            existingItem.setIsTaxable(payrollItemDto.getIsTaxable());
            existingItem.setDescription(payrollItemDto.getDescription());
            existingItem.setCategory(payrollItemDto.getCategory());
            existingItem.setGovernmentCode(payrollItemDto.getGovernmentCode());
            
            PayrollItem updatedItem = payrollItemRepository.save(existingItem);
            log.info("Successfully updated payroll item with ID: {}", updatedItem.getId());
            
            return ApiResponse.success("Payroll item updated successfully", convertToDto(updatedItem));
            
        } catch (Exception e) {
            log.error("Error updating payroll item: {}", e.getMessage());
            return ApiResponse.error("Failed to update payroll item: " + e.getMessage());
        }
    }
    
    // Deactivate payroll item (soft delete)
    public ApiResponse<Void> deactivatePayrollItem(Long id) {
        try {
            log.info("Deactivating payroll item with ID: {}", id);
            PayrollItem item = payrollItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payroll item not found"));
            
            item.setIsActive(false);
            payrollItemRepository.save(item);
            log.info("Successfully deactivated payroll item with ID: {}", id);
            
            return ApiResponse.success("Payroll item deactivated successfully", null);
            
        } catch (Exception e) {
            log.error("Error deactivating payroll item: {}", e.getMessage());
            return ApiResponse.error("Failed to deactivate payroll item: " + e.getMessage());
        }
    }
    
    // Search payroll items
    public ApiResponse<List<PayrollItemDto>> searchPayrollItems(String searchTerm) {
        try {
            log.info("Searching payroll items with term: {}", searchTerm);
            List<PayrollItem> items = payrollItemRepository.findBySearchTerm(searchTerm);
            List<PayrollItemDto> dtoList = items.stream().map(this::convertToDto).toList();
            return ApiResponse.success("Search results retrieved successfully", dtoList);
        } catch (Exception e) {
            log.error("Error searching payroll items: {}", e.getMessage());
            return ApiResponse.error("Failed to search payroll items: " + e.getMessage());
        }
    }
    
    // Convert entity to DTO
    private PayrollItemDto convertToDto(PayrollItem payrollItem) {
        return PayrollItemDto.builder()
                .id(payrollItem.getId())
                .name(payrollItem.getName())
                .code(payrollItem.getCode())
                .type(payrollItem.getType())
                .isActive(payrollItem.getIsActive())
                .isMandatory(payrollItem.getIsMandatory())
                .isPercentage(payrollItem.getIsPercentage())
                .fixedAmount(payrollItem.getFixedAmount())
                .percentageRate(payrollItem.getPercentageRate())
                .minimumAmount(payrollItem.getMinimumAmount())
                .maximumAmount(payrollItem.getMaximumAmount())
                .isTaxable(payrollItem.getIsTaxable())
                .description(payrollItem.getDescription())
                .category(payrollItem.getCategory())
                .governmentCode(payrollItem.getGovernmentCode())
                .createdAt(payrollItem.getCreatedAt())
                .updatedAt(payrollItem.getUpdatedAt())
                .build();
    }
}
