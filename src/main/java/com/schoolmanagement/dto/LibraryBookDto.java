package com.schoolmanagement.dto;

import com.schoolmanagement.entity.LibraryBook;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryBookDto {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;
    
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;
    
    @Size(max = 255, message = "Publisher must not exceed 255 characters")
    private String publisher;
    
    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 2100, message = "Publication year must not exceed 2100")
    private Integer publicationYear;
    
    @Size(max = 50, message = "Edition must not exceed 50 characters")
    private String edition;
    
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;
    
    @Size(max = 100, message = "Subject must not exceed 100 characters")
    private String subject;
    
    @NotNull(message = "Total copies is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;
    
    @NotNull(message = "Available copies is required")
    @Min(value = 0, message = "Available copies cannot be negative")
    private Integer availableCopies;
    
    @Min(value = 0, message = "Borrowed copies cannot be negative")
    private Integer borrowedCopies;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
    
    private LibraryBook.BookStatus status;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Helper methods
    public boolean isAvailable() {
        return availableCopies != null && availableCopies > 0;
    }
    
    public boolean canBorrow() {
        return isAvailable() && status == LibraryBook.BookStatus.AVAILABLE;
    }
    
    public boolean isOverdue() {
        return status == LibraryBook.BookStatus.BORROWED;
    }
}
