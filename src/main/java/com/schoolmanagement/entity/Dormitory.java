package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dorm_master_id")
    private Teacher dormMaster; // The teacher assigned as dorm master

    @OneToMany(mappedBy = "dormitory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DormitoryRoom> rooms;

    @Column(nullable = false)
    private Integer capacity; // Total capacity

    @Column(nullable = false)
    private Integer currentOccupancy = 0; // Current number of students

    @Column(nullable = false)
    private Boolean isActive = true;

    private String rules;
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}