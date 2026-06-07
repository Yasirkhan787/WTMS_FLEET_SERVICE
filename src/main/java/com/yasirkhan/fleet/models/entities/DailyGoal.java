package com.yasirkhan.fleet.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "WTMS_DAILY_GOALS")
public class DailyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tehsil_id", nullable = false)
    private Tehsil tehsil;              // e.g., The ID for "Islamabad I9"

    @Column(nullable = false)
    private LocalDate targetDate; // e.g., 2026-06-06

    @Column(nullable = false)
    private Double targetTonnage; // e.g., 150.0

    @Column(nullable = false)
    private String assignedBy; // Admin username/ID
}