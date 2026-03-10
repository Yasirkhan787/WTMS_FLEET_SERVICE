package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.responses.DriverResponse;
import com.yasirkhan.fleet.services.DriverService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fleet/driver")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PatchMapping("/update/{id}")

    public ResponseEntity<Void> patchDriver(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates) {

        driverService.updateDriver(id, updates);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DriverResponse>> getAllDrivers(){

        return
                ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable UUID id){

        return
                ResponseEntity.ok(driverService.getDriverById(id));
    }

}
