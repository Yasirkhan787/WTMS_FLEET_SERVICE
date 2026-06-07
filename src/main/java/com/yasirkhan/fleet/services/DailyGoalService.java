package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.requests.DailyGoalRequest;
import com.yasirkhan.fleet.responses.DailyGoalResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DailyGoalService {
    DailyGoalResponse assignGoal(DailyGoalRequest request);
    Double getTargetTonnage(UUID tehsilId, LocalDate date);
    List<DailyGoalResponse> getAllGoalsForTehsil(UUID tehsilId);
    List<DailyGoalResponse> getAllGoals();
}