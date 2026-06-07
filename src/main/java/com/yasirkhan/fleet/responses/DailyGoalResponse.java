package com.yasirkhan.fleet.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class DailyGoalResponse {
    private UUID goalId;
    private UUID tehsilId;
    private String tehsilName; // Helpful for the frontend Admin table!
    private LocalDate targetDate;
    private Double targetTonnage;
    private String assignedBy;
}