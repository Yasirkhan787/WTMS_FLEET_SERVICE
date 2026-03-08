package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.services.DriverService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PatchMapping("/update/{userID}")

    public ResponseEntity<Void> patchDriver(
            @PathVariable UUID userID,
            @RequestBody Map<String, Object> updates) {

        driverService.updateDriver(userID, updates);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DriverDto>> getAllDrivers(){

        return
                ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{userID}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable UUID userID){

        return
                ResponseEntity.ok(driverService.getDriverById(userID));
    }

}
