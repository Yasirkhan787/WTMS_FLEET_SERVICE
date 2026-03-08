package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.services.DriverService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<DriverDto>> getAllDrivers(){

        return
                ResponseEntity.ok(driverService.getAllDrivers());
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable UUID id){

        return
                ResponseEntity.ok(driverService.getDriverById(id));
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestBody Status status){

        driverService.updateStatus(id, status);

        return
                ResponseEntity.ok("Driver Status: " + status + "Updated.");
    }
}
