package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.requests.VehicleRequest;
import com.yasirkhan.fleet.responses.VehicleResponse;
import com.yasirkhan.fleet.services.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/fleet/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> addVehicle(@RequestBody VehicleRequest request) {
        return
                new ResponseEntity<>(vehicleService.addVehicle(request), HttpStatus.CREATED);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity updateVehicle(@RequestBody Map<String, Object> updates) {

        vehicleService.updateVehicle(updates);

        return new
                ResponseEntity<>("Vehicle with ID:" + updates.get("vehicleId") + "Updated Successfully",
                        HttpStatus.NO_CONTENT);
    }

    @PostMapping("/block/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity blockVehicle(@PathVariable UUID vehicleId, @RequestParam Boolean blockStatus ) {

        vehicleService.blockVehicle(vehicleId, blockStatus);

        return new
                ResponseEntity<>("Vehicle with ID:" + vehicleId + "Blocked Successfully",
                HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<VehicleResponse>> getAll(){
        return
                new ResponseEntity<>(vehicleService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable UUID id){
        return
                new ResponseEntity<>(vehicleService.getVehicleById(id),HttpStatus.OK);
    }
}
