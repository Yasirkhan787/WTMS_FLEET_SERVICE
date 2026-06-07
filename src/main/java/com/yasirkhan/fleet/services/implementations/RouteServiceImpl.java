package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.exceptions.ResourceAlreadyExistException;
import com.yasirkhan.fleet.exceptions.ResourceNotFoundException;
import com.yasirkhan.fleet.models.dtos.RouteResponseEventDto;
import com.yasirkhan.fleet.models.entities.Route;
import com.yasirkhan.fleet.models.entities.Tehsil;
import com.yasirkhan.fleet.models.entities.Yard;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.EventType;
import com.yasirkhan.fleet.models.enums.Status;
import com.yasirkhan.fleet.repositories.RouteRepository;
import com.yasirkhan.fleet.repositories.TehsilRepository;
import com.yasirkhan.fleet.repositories.YardRepository;
import com.yasirkhan.fleet.requests.RouteRequest;
import com.yasirkhan.fleet.requests.RouteUpdateRequest;
import com.yasirkhan.fleet.responses.RouteResponse;
import com.yasirkhan.fleet.services.RouteService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import com.yasirkhan.fleet.utils.SpatialUtils;
import org.locationtech.jts.geom.LineString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final TehsilRepository tehsilRepository;
    private final YardRepository yardRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    public RouteServiceImpl(RouteRepository routeRepository, TehsilRepository tehsilRepository, YardRepository yardRepository,
                            ApplicationEventPublisher eventPublisher,
                            RedisTemplate<String, Object> redisTemplate) {
        this.routeRepository = routeRepository;
        this.tehsilRepository = tehsilRepository;
        this.yardRepository = yardRepository;
        this.eventPublisher = eventPublisher;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public RouteResponse addRoute(RouteRequest request) {

        Tehsil tehsil = tehsilRepository.findById(request.getTehsilId())
                .orElseThrow(() -> new ResourceNotFoundException("Tehsil not found"));
        Yard source = yardRepository.findById(request.getSourceYardId())
                .orElseThrow(() -> new ResourceNotFoundException("Source yard not found"));
        Yard dest = yardRepository.findById(request.getDestinationYardId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination yard not found"));

        LineString path = SpatialUtils.toLineString(request.getPath());

        if (routeRepository.existsByPathEquals(path)) {
            throw new ResourceAlreadyExistException("This exact geographic route has already been mapped.");
        }

        Route route = new Route();
        route.setTehsil(tehsil);
        route.setSourceYard(source);
        route.setDestinationYard(dest);
        route.setRouteName(request.getRouteName());
        route.setPath(path);
        route.setEstimatedTime(request.getEstimatedTime());
        route.setEstimatedDistance(request.getEstimatedDistance());
        route.setStatus(Status.ACTIVE);

        try {
            Route savedRoute = routeRepository.saveAndFlush(route);

            syncRouteToRedis(savedRoute);

            RouteResponse response = ResponseConversion.toRouteResponse(savedRoute);

            publishRouteEvent(EventType.CREATE, EventStatus.SUCCESS, response);

            return response;
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to add route: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateRoute(UUID routeId, RouteUpdateRequest request) {

        Route dbRoute = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route with ID: " + routeId + " Not Found"));

        if (request.getRouteName() != null) dbRoute.setRouteName(request.getRouteName());

        if (request.getTehsilId() != null) {
            Tehsil tehsil = tehsilRepository.findById(request.getTehsilId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tehsil not found"));
            dbRoute.setTehsil(tehsil);
        }

        if (request.getSourceYardId() != null) {
            Yard source = yardRepository.findById(request.getSourceYardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Source Yard not found"));
            dbRoute.setSourceYard(source);
        }

        if (request.getDestinationYardId() != null) {
            Yard dest = yardRepository.findById(request.getDestinationYardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dest Yard not found"));
            dbRoute.setDestinationYard(dest);
        }

        if (request.getPath() != null) {
            LineString newPath = SpatialUtils.toLineString(request.getPath());
            if (routeRepository.existsByPathEquals(newPath)) {
                throw new ResourceAlreadyExistException("This exact geographic route has already been mapped.");
            }
            dbRoute.setPath(newPath);
        }

        if (request.getEstimatedTime() != null) dbRoute.setEstimatedTime(request.getEstimatedTime());
        if (request.getEstimatedDistance() != null) dbRoute.setEstimatedDistance(request.getEstimatedDistance());
        if (request.getStatus() != null) dbRoute.setStatus(request.getStatus());

        try {
            Route updatedRoute = routeRepository.saveAndFlush(dbRoute);

            syncRouteToRedis(updatedRoute);

            RouteResponse response = ResponseConversion.toRouteResponse(updatedRoute);

            publishRouteEvent(EventType.UPDATE, EventStatus.SUCCESS, response);
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to update route: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void blockRoute(UUID routeId, Boolean blockStatus) {
        Route dbRoute = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route with ID: " + routeId + " Not Found"));

        Status newStatus = blockStatus ? Status.BLOCKED : Status.ACTIVE;
        dbRoute.setStatus(newStatus);

        try {
            Route updatedRoute = routeRepository.saveAndFlush(dbRoute);

            // Sync the new status to Redis
            syncRouteToRedis(updatedRoute);

            RouteResponse response = ResponseConversion.toRouteResponse(updatedRoute);
            publishRouteEvent(EventType.UPDATE, EventStatus.SUCCESS, response);
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to block route: " + e.getMessage());
        }
    }

    @Override
    public RouteResponse getRouteByID(UUID routeId) {
        Route dbRoute = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route with ID: " + routeId + " Not Found"));
        return ResponseConversion.toRouteResponse(dbRoute);
    }

    @Override
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(ResponseConversion::toRouteResponse)
                .collect(Collectors.toList());
    }

    // --- Redis Sync Helper (Route) ---
    private void syncRouteToRedis(Route route) {
        String redisKey = "wtms:route:" + route.getRouteId().toString();
        Map<String, Object> data = new HashMap<>();

        data.put("routeName", route.getRouteName());
        data.put("tehsilId", route.getTehsil().getTehsilId().toString());
        data.put("sourceYardId", route.getSourceYard().getId().toString());
        data.put("destinationYardId", route.getDestinationYard().getId().toString());

        // Convert PostGIS LineString to standard Well-Known Text (WKT) string
        if (route.getPath() != null) {
            data.put("path", SpatialUtils.toPolyLine(route.getPath()));
        }

        data.put("estimatedDistance", String.valueOf(route.getEstimatedDistance()));
        data.put("estimatedTime", route.getEstimatedTime());
        // data.put("estimatedFuel", String.valueOf(route.getEstimatedFuel()));
        data.put("status", route.getStatus().name());

        redisTemplate.opsForHash().putAll(redisKey, data);
    }

    // --- Kafka Publisher Helper ---
    private void publishRouteEvent(EventType type, EventStatus status, RouteResponse response) {
        RouteResponseEventDto eventDto = RouteResponseEventDto.builder()
                .type(type)
                .eventTypeStatus(status)
                .routeData(response)
                .build();
        eventPublisher.publishEvent(eventDto);
    }
}