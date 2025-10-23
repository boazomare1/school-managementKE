package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "library_books")
public class LibraryBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String isbn;

    private String publisher;

    private Integer publicationYear;

    private String edition;

    private String category;

    private String subject;

    @Column(nullable = false)
    private Integer totalCopies;

    @Column(nullable = false)
    private Integer availableCopies;

    @Column(nullable = false)
    private Integer borrowedCopies = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String description;

    private String location; // Shelf location

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status; // AVAILABLE, BORROWED, RESERVED, DAMAGED, LOST

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum BookStatus {
        AVAILABLE,
        BORROWED,
        RESERVED,
        DAMAGED,
        LOST
    }
}

