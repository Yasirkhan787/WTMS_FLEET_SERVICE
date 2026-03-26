package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.models.entities.Route;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.repositories.RouteRepository;
import com.yasirkhan.fleet.requests.RouteRequest;
import com.yasirkhan.fleet.responses.RouteResponse;
import com.yasirkhan.fleet.services.RouteService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import com.yasirkhan.fleet.utils.SpatialUtils;
import jakarta.transaction.Transactional;
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

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional
    public RouteResponse addRoute(RouteRequest routeRequest) {

        LineString path =
                SpatialUtils.toLineString(routeRequest.getPath());

        Route route = new Route();

        route.setRouteName(routeRequest.getRouteName());
        route.setPath(path);
        route.setEstimatedTime(routeRequest.getEstimatedTime());
        route.setEstimatedDistance(routeRequest.getEstimatedDistance());
        route.setStatus(routeRequest.getStatus());

        Route savedRoute = null;

        try {
            savedRoute = routeRepository.save(route);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }

        return ResponseConversion
                .toRouteResponse(savedRoute);
    }


    @Override
    @Transactional
    public void updateRoute(Map<String, Object> updates) {

        UUID routeId =  UUID.fromString(updates.get("routeId").toString());

        Route dbRoute =
                routeRepository
                        .findById(routeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Route with ID: " + routeId +"Not Found"));

        updates.forEach((key, value) -> {
                switch (key){
                    case "routeName" -> dbRoute.setRouteName((String) value);
                    case "path" -> dbRoute.setPath(SpatialUtils.toLineString((String) value));
                    case "estimatedTime" -> dbRoute.setEstimatedTime((String) value);
                    case "estimatedDistance" -> dbRoute.setEstimatedDistance((String) value);
                    case "status" -> dbRoute.setStatus((Status) value);
                }
        });
    }

    @Override
    public void blockRoute(UUID routeId, Boolean blockStatus) {

        Route dbRoute =
                routeRepository
                        .findById(routeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Route with ID: " + routeId +"Not Found"));

        String status = blockStatus ? "BLOCKED" : "ACTIVE";

        dbRoute.setStatus(Status.valueOf(status));

        Route savedRoute = null;
        try {
            savedRoute = routeRepository.save(dbRoute);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public RouteResponse getRouteByID(UUID routeId) {

        Route dbRoute =
                routeRepository
                        .findById(routeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Route with ID: " + routeId +"Not Found"));

        return ResponseConversion
                .toRouteResponse(dbRoute);
    }

    @Override
    public List<RouteResponse> getAllRoutes() {

        List<Route> dbRoutes =
                routeRepository.findAll();

        if (dbRoutes.isEmpty()) {
            throw new ResourceNotFoundException("No Route Found in Database");
        }

        return dbRoutes
                .stream()
                .map(ResponseConversion::toRouteResponse)
                .collect(Collectors.toList());
    }
}
