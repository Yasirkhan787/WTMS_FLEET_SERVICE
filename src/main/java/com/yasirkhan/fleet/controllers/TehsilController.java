package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.models.entities.Tehsil;
import com.yasirkhan.fleet.requests.TehsilRequest;
import com.yasirkhan.fleet.requests.VehicleUpdateRequest;
import com.yasirkhan.fleet.responses.TehsilResponse;
import com.yasirkhan.fleet.responses.TehsilWithYardsResponse;
import com.yasirkhan.fleet.services.TehsilService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/fleet/tehsils")
public class TehsilController {

    private final TehsilService tehsilService;

    public TehsilController(TehsilService tehsilService) {
        this.tehsilService = tehsilService;
    }

    /**
     * Admin Action: Create a new territory (Tehsil)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TehsilResponse> createTehsil(@Valid @RequestBody TehsilRequest request) {
        TehsilResponse response = tehsilService.createTehsil(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Update Tehsil
    @PatchMapping("/{tehsilId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateTehsil(@PathVariable UUID tehsilId, @RequestBody TehsilRequest updates) {
        tehsilService.updateTehsil(tehsilId, updates);
        return new ResponseEntity<>("Tehsil with Tehsil Id: " + tehsilId + " Updated Successfully", HttpStatus.NO_CONTENT);
    }

    // Block Tehsil
    @PatchMapping("/block/{tehsilId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> blockTehsil(@PathVariable UUID tehsilId, @RequestParam Boolean blockStatus ) {
        tehsilService.blockTehsil(tehsilId, blockStatus);
        return new ResponseEntity<>("Tehsil with Tehsil Id: " + tehsilId + " Blocked Successfully", HttpStatus.NO_CONTENT);
    }

    /**
     * System Action: Get all Tehsils
     */
    @GetMapping
    public ResponseEntity<List<TehsilResponse>> getAllTehsils() {
        List<TehsilResponse> responses = tehsilService.getAllTehsils();
        return ResponseEntity.ok(responses);
    }

    /**
     * System Action: Get specific Tehsil details
     */
    @GetMapping("/{tehsilId}")
    public ResponseEntity<TehsilResponse> getTehsilById(@PathVariable UUID tehsilId) {
        TehsilResponse response = tehsilService.getTehsilById(tehsilId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-yards")
    public ResponseEntity<List<TehsilWithYardsResponse>> getAllTehsilsWithYards() {
        // You will need to implement this in TehsilService
        return ResponseEntity.ok(tehsilService.getAllTehsilsWithYards());
    }
}