package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.requests.YardRequest;
import com.yasirkhan.fleet.requests.YardUpdateRequest;
import com.yasirkhan.fleet.responses.YardResponse;
import com.yasirkhan.fleet.services.YardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/fleet/yards")
public class YardController {

    private final YardService yardService;

    public YardController(YardService yardService) {
        this.yardService = yardService;
    }

    /**
     * Create a new Yard (Supports both RADIUS and POLYGON boundary types)
     */
    @PostMapping
    public ResponseEntity<YardResponse> createYard(@Valid @RequestBody YardRequest request) {
        YardResponse response = yardService.createYard(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing Yard (Includes geometry overwrite logic)
     */
    @PutMapping("/{yardId}")
    public ResponseEntity<YardResponse> updateYard(
            @PathVariable UUID yardId,
            @Valid @RequestBody YardUpdateRequest request) {
        YardResponse response = yardService.updateYard(yardId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific Yard by its ID
     */
    @GetMapping("/{yardId}")
    public ResponseEntity<YardResponse> getYardById(@PathVariable UUID yardId) {
        YardResponse response = yardService.getYardById(yardId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active and inactive Yards
     */
    @GetMapping
    public ResponseEntity<List<YardResponse>> getAllYards() {
        List<YardResponse> responses = yardService.getAllYards();
        return ResponseEntity.ok(responses);
    }

    // Inside YardController.java

    /**
     * Get all active and inactive Yards for a specific Tehsil
     */
    @GetMapping("/tehsil/{tehsilId}")
    public ResponseEntity<List<YardResponse>> getAllYardsByTehsil(@PathVariable UUID tehsilId) {
        List<YardResponse> responses = yardService.getAllYardsByTehsil(tehsilId);
        return ResponseEntity.ok(responses);
    }
}