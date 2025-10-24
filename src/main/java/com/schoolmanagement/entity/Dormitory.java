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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dormitories")
public class Dormitory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Dormitory A, Dormitory B, etc.

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location; // Physical location of the dormitory

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dorm_master_id")
    private Teacher dormMaster; // The teacher assigned as dorm master

    @OneToMany(mappedBy = "dormitory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DormitoryRoom> rooms;

    @Column(nullable = false)
    private Integer capacity; // Total capacity

    @Column(nullable = false)
    private Integer totalRooms = 0; // Total number of rooms

    @Column(nullable = false)
    private Integer currentOccupancy = 0; // Current number of students

    @Column(nullable = false)
    private Integer availableRooms = 0; // Available rooms for new students

    @Column(nullable = false)
    private Integer occupiedRooms = 0; // Currently occupied rooms

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyFee; // Monthly fee for dormitory

    @Column(nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DormitoryStatus status = DormitoryStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DormitoryType type = DormitoryType.MALE;

    private String rules;
    private String notes;

    public enum DormitoryType {
        MALE,
        FEMALE,
        MIXED
    }

    public enum DormitoryStatus {
        ACTIVE,
        MAINTENANCE,
        CLOSED
    }

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}