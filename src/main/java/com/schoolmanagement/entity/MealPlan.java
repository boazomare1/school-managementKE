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
@Table(name = "meal_plans")
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType; // BREAKFAST, LUNCH, DINNER, SNACK

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer soldQuantity = 0;

    @Column(nullable = false)
    private LocalDateTime mealDate;

    @Column(nullable = false)
    private LocalDateTime servingTime;

    private String ingredients;

    private String allergens;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealStatus status; // AVAILABLE, SOLD_OUT, CANCELLED

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum MealType {
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACK
    }

    public enum MealStatus {
        AVAILABLE,
        SOLD_OUT,
        CANCELLED
    }
}

