package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDto {
    
    private Long id;
    
    @NotBlank(message = "School name is required")
    @Size(max = 100, message = "School name must not exceed 100 characters")
    private String name;
    
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 50, message = "Website must not exceed 50 characters")
    private String website;
    
    @Size(max = 20, message = "Registration number must not exceed 20 characters")
    private String registrationNumber;
    
    @Size(max = 100, message = "Principal name must not exceed 100 characters")
    private String principalName;
    
    @Size(max = 20, message = "Principal phone must not exceed 20 characters")
    private String principalPhone;
    
    @Email(message = "Principal email should be valid")
    @Size(max = 100, message = "Principal email must not exceed 100 characters")
    private String principalEmail;
    
    private Boolean isActive;
}


