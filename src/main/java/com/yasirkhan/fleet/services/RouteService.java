package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.requests.RouteRequest;
import com.yasirkhan.fleet.responses.RouteResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RouteService {

    RouteResponse addRoute(RouteRequest routeRequest);

    RouteResponse getRouteByID(UUID routeId);

    List<RouteResponse> getAllRoutes();

    void updateRoute(Map<String, Object> updates);

    void blockRoute(UUID routeId, Boolean blockStatus);
}
