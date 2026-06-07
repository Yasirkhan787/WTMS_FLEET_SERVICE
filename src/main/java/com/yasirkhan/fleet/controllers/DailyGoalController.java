package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.requests.DailyGoalRequest;
import com.yasirkhan.fleet.responses.DailyGoalResponse;
import com.yasirkhan.fleet.services.DailyGoalService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/fleet/goals")
public class DailyGoalController {

    private final DailyGoalService dailyGoalService;

    public DailyGoalController(DailyGoalService dailyGoalService) {
        this.dailyGoalService = dailyGoalService;
    }

    /**
     * Admin creates or updates a goal for a specific Tehsil and date.
     */
    @PostMapping
    public ResponseEntity<DailyGoalResponse> assignGoal(@Valid @RequestBody DailyGoalRequest request) {
        DailyGoalResponse response = dailyGoalService.assignGoal(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all historical goals for a specific Tehsil (For the Admin UI table)
     */
    @GetMapping("/tehsil/{tehsilId}")
    public ResponseEntity<List<DailyGoalResponse>> getGoalsForTehsil(@PathVariable UUID tehsilId) {
        List<DailyGoalResponse> responses = dailyGoalService.getAllGoalsForTehsil(tehsilId);
        return ResponseEntity.ok(responses);
    }

    /**
     * MICROSERVICE ENDPOINT: Fetches purely the Double value for cross-service calculations.
     * Called by TripService via RestTemplate.
     */
    @GetMapping("/target")
    public ResponseEntity<Double> getTargetTonnageForTehsil(
            @RequestParam UUID tehsilId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Double target = dailyGoalService.getTargetTonnage(tehsilId, date);
        return ResponseEntity.ok(target);
    }

    /**
     * Get ALL historical goals across all territories (For the global Admin Dashboard)
     */
    @GetMapping
    public ResponseEntity<List<DailyGoalResponse>> getAllGoals() {
        List<DailyGoalResponse> responses = dailyGoalService.getAllGoals();
        return ResponseEntity.ok(responses);
    }
}