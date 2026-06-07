package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.requests.TehsilRequest;
import com.yasirkhan.fleet.responses.TehsilResponse;
import com.yasirkhan.fleet.responses.TehsilWithYardsResponse;
import com.yasirkhan.fleet.services.TehsilService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TehsilResponse> createTehsil(@Valid @RequestBody TehsilRequest request) {
        TehsilResponse response = tehsilService.createTehsil(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * System Action: Get all Tehsils (Used to populate UI dropdowns)
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