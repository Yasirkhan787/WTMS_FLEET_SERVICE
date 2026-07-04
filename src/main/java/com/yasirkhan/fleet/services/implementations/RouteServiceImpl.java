package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.exceptions.ResourceAlreadyExistException;
import com.yasirkhan.fleet.exceptions.ResourceNotFoundException;
import com.yasirkhan.fleet.models.UserPrincipal;
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
import com.yasirkhan.fleet.responses.YardResponse;
import com.yasirkhan.fleet.services.RouteService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import com.yasirkhan.fleet.utils.SpatialUtils;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.LineString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

            YardResponse sourceYardResponse = ResponseConversion.toYardResponse(source);
            YardResponse destYardResponse = ResponseConversion.toYardResponse(dest);

            publishRouteEvent(EventType.CREATE, EventStatus.SUCCESS, response, sourceYardResponse,destYardResponse);

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

        Yard source = null;
        if (request.getSourceYardId() != null) {
            source = yardRepository.findById(request.getSourceYardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Source Yard not found"));
            dbRoute.setSourceYard(source);
        }

        Yard dest = null;
        if (request.getDestinationYardId() != null) {
            dest = yardRepository.findById(request.getDestinationYardId())
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

            YardResponse sourceYardResponse = ResponseConversion.toYardResponse(source);
            YardResponse destYardResponse = ResponseConversion.toYardResponse(dest);

            publishRouteEvent(EventType.UPDATE, EventStatus.SUCCESS, response, sourceYardResponse,destYardResponse);
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

        YardResponse sourceYardResponse = ResponseConversion.toYardResponse(dbRoute.getSourceYard());
        YardResponse destYardResponse = ResponseConversion.toYardResponse(dbRoute.getDestinationYard());
        try {
            Route updatedRoute = routeRepository.saveAndFlush(dbRoute);

            // Sync the new status to Redis
            syncRouteToRedis(updatedRoute);

            RouteResponse response = ResponseConversion.toRouteResponse(updatedRoute);
            publishRouteEvent(EventType.UPDATE, EventStatus.SUCCESS, response, sourceYardResponse,destYardResponse);
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized: No valid session found.");
        }

        // Extracting user details using your UserPrincipal syntax
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String userId = principal.userId();
        String role = principal.role();

        List<Route> dbRoutes;

        if ("ADMIN".equals(role)) {
            dbRoutes = routeRepository.findAll();

        } else if ("SUPERVISOR".equals(role)) {
            String tehsilId = (String) redisTemplate.opsForHash().get("wtms:user:" + userId, "tehsilId");
            if (tehsilId == null || tehsilId.isEmpty()) {
                throw new ResourceNotFoundException("No territory assigned to this supervisor.");
            }

            dbRoutes = routeRepository.findByTehsil_TehsilId(UUID.fromString(tehsilId));

        } else {
            // Block any other roles (like DRIVER) from viewing routes
            throw new RuntimeException("You do not have permission to view routes.");
        }

        return dbRoutes.stream()
                .map(ResponseConversion::toRouteResponse)
                .collect(Collectors.toList());
    }

    // Helper Methods
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
    private void publishRouteEvent(EventType type, EventStatus status, RouteResponse response, YardResponse source, YardResponse destination) {
        RouteResponseEventDto eventDto = RouteResponseEventDto.builder()
                .type(type)
                .eventTypeStatus(status)
                .routeData(response)
                .sourceYardData(source)
                .destinationYardData(destination)
                .build();
        eventPublisher.publishEvent(eventDto);
    }
}