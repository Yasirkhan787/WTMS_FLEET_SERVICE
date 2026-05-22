package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.models.dtos.RouteResponseEventDto;
import com.yasirkhan.fleet.models.entities.Route;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.producers.RouteEventProducer;
import com.yasirkhan.fleet.repositories.RouteRepository;
import com.yasirkhan.fleet.requests.RouteRequest;
import com.yasirkhan.fleet.responses.RouteResponse;
import com.yasirkhan.fleet.services.RouteService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import com.yasirkhan.fleet.utils.SpatialUtils;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteEventProducer producer;

    public RouteServiceImpl(RouteRepository routeRepository, RouteEventProducer producer) {
        this.routeRepository = routeRepository;
        this.producer = producer;
    }

    @Override
    @Transactional
    public RouteResponse addRoute(RouteRequest request) {

        LineString path = SpatialUtils.toLineString(request.getPath());

        // Check for duplicate Geographic Path
        if (routeRepository.existsByPathEquals(path)) {
            throw new DuplicateResourceException("This exact geographic route has already been mapped.");
        }

        Route route = new Route();
        route.setRouteName(request.getRouteName());

        // Set the Origin details
        route.setOrigin(request.getOrigin());
        route.setOriginLat(request.getOriginCoords().getLat());
        route.setOriginLng(request.getOriginCoords().getLng());

        // Set the Destination details
        route.setDestination(request.getDestination());
        route.setDestinationLat(request.getDestinationCoords().getLat());
        route.setDestinationLng(request.getDestinationCoords().getLng());

        route.setPath(path);
        route.setEstimatedTime(request.getEstimatedTime());
        route.setEstimatedDistance(request.getEstimatedDistance());
        route.setStatus(Status.ACTIVE);

        Route savedRoute;

        try {
            savedRoute = routeRepository.save(route);
            // Send event to kafka
            RouteResponseEventDto eventDto =
                    RouteResponseEventDto
                            .builder()
                            .routeId(savedRoute.getRouteId())
                            .status("SUCCESS")
                            .type("CREATE")
                            .message("Route added successfully")
                            .build();
            producer.sendRouteResponseEvent(eventDto);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }

        return ResponseConversion.toRouteResponse(savedRoute);
    }

    // Update Route
    @Override
    @Transactional
    public void updateRoute(Map<String, Object> updates) {

        UUID routeId = UUID.fromString(updates.get("routeId").toString());

        Route dbRoute = routeRepository
                .findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route with ID: " + routeId + " Not Found"));

        updates.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "routeName" -> dbRoute.setRouteName((String) value);

                    case "origin" -> dbRoute.setOrigin((String) value);
                    case "originCoords" -> {
                        Map<?, ?> coords = (Map<?, ?>) value;
                        dbRoute.setOriginLat(((Number) coords.get("lat")).doubleValue());
                        dbRoute.setOriginLng(((Number) coords.get("lng")).doubleValue());
                    }

                    case "destination" -> dbRoute.setDestination((String) value);
                    case "destinationCoords" -> {
                        Map<?, ?> coords = (Map<?, ?>) value;
                        dbRoute.setDestinationLat(((Number) coords.get("lat")).doubleValue());
                        dbRoute.setDestinationLng(((Number) coords.get("lng")).doubleValue());
                    }

                    case "path" -> {
                        if (routeRepository.existsByPathEquals(SpatialUtils.toLineString((String) value))) {
                            throw new DuplicateResourceException("This exact geographic route has already been mapped.");
                        }
                        dbRoute.setPath(SpatialUtils.toLineString((String) value));
                    }
                    case "estimatedTime" -> dbRoute.setEstimatedTime((String) value);
                    case "estimatedDistance" -> dbRoute.setEstimatedDistance((String) value);
                    case "status" -> dbRoute.setStatus(Status.valueOf(value.toString()));
                }
            }
        });

        try {
            routeRepository.saveAndFlush(dbRoute);
            // Send event to kafka
            RouteResponseEventDto eventDto =
                    RouteResponseEventDto
                            .builder()
                            .routeId(dbRoute.getRouteId())
                            .status("SUCCESS")
                            .type("UPDATE")
                            .message("Route updated successfully")
                            .build();
            producer.sendRouteResponseEvent(eventDto);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    // Block Route
    @Override
    public void blockRoute(UUID routeId, Boolean blockStatus) {

        Route dbRoute = routeRepository
                .findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route with ID: " + routeId + " Not Found"));

        String status = blockStatus ? "BLOCKED" : "ACTIVE";
        dbRoute.setStatus(Status.valueOf(status));

        try {
            routeRepository.save(dbRoute);
            // Send event to kafka
            RouteResponseEventDto eventDto =
                    RouteResponseEventDto
                            .builder()
                            .routeId(dbRoute.getRouteId())
                            .status(status)
                            .type("STATUS_UPDATE")
                            .message("Route Status { " + status + " } updated successfully")
                            .build();
            producer.sendRouteResponseEvent(eventDto);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    // GetRoute By ID
    @Override
    public RouteResponse getRouteByID(UUID routeId) {
        Route dbRoute = routeRepository
                .findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route with ID: " + routeId + " Not Found"));

        return ResponseConversion.toRouteResponse(dbRoute);
    }

    // Get All Routes
    @Override
    public List<RouteResponse> getAllRoutes() {
        List<Route> dbRoutes = routeRepository.findAll();

        if (dbRoutes.isEmpty()) {
            throw new ResourceNotFoundException("No Routes Found in Database");
        }

        return dbRoutes.stream()
                .map(ResponseConversion::toRouteResponse)
                .collect(Collectors.toList());
    }
}