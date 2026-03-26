package com.yasirkhan.fleet.controllers;

import com.yasirkhan.fleet.requests.RouteRequest;
import com.yasirkhan.fleet.responses.RouteResponse;
import com.yasirkhan.fleet.services.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/fleet/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteResponse> addRoute(@RequestBody RouteRequest routeRequest) {

        return ResponseEntity
                .ok(routeService.addRoute(routeRequest));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity updateRoute(@RequestBody Map<String, Object> updates) {

        routeService.updateRoute(updates);

        return new
                ResponseEntity<>("Route with ID:" + updates.get("routeId") + "Updated Successfully",
                HttpStatus.NO_CONTENT);
    }

    @PostMapping("/block/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity blockRoute(@PathVariable UUID id, @RequestParam Boolean blockStatus ) {

        routeService.blockRoute(id, blockStatus);

        return new
                ResponseEntity<>("Route with ID:" + id + "Blocked Successfully",
                HttpStatus.NO_CONTENT);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<RouteResponse> getRoute(@PathVariable UUID id) {

        return ResponseEntity
                .ok(routeService.getRouteByID(id));
    }

    @GetMapping("all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {

        return ResponseEntity
                .ok(routeService.getAllRoutes());
    }

}
