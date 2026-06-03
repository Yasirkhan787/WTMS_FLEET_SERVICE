package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.requests.VehicleRequest;
import com.yasirkhan.fleet.requests.VehicleUpdateRequest;
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

    @PatchMapping("/update/{vehicleNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateVehicle(@PathVariable String vehicleNo, @RequestBody VehicleUpdateRequest updates) {

        vehicleService.updateVehicle(vehicleNo, updates);

        return new
                ResponseEntity<>("Vehicle with Vehicle No:" + vehicleNo + "Updated Successfully",
                        HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/block/{vehicleNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> blockVehicle(@PathVariable String vehicleNo, @RequestParam Boolean blockStatus ) {

        vehicleService.blockVehicle(vehicleNo, blockStatus);

        return new
                ResponseEntity<>("Vehicle with vehicle No:" + vehicleNo + "Blocked Successfully",
                HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<VehicleResponse>> getAll(){
        return
                new ResponseEntity<>(vehicleService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/{vehicleNo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable String vehicleNo){
        return
                new ResponseEntity<>(vehicleService.getVehicleById(vehicleNo),HttpStatus.OK);
    }
}
