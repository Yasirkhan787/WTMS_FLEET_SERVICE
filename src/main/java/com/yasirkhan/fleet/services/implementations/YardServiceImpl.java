package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.exceptions.ResourceNotFoundException;
import com.yasirkhan.fleet.models.entities.Tehsil;
import com.yasirkhan.fleet.models.entities.Yard;
import com.yasirkhan.fleet.models.enums.Status;
import com.yasirkhan.fleet.repositories.TehsilRepository;
import com.yasirkhan.fleet.repositories.YardRepository;
import com.yasirkhan.fleet.requests.YardRequest;
import com.yasirkhan.fleet.requests.YardUpdateRequest;
import com.yasirkhan.fleet.responses.YardResponse;
import com.yasirkhan.fleet.services.YardService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import com.yasirkhan.fleet.utils.SpatialUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class YardServiceImpl implements YardService {

    private final YardRepository yardRepository;
    private final TehsilRepository tehsilRepository;

    public YardServiceImpl(YardRepository yardRepository, TehsilRepository tehsilRepository) {
        this.yardRepository = yardRepository;
        this.tehsilRepository = tehsilRepository;
    }

    @Override
    @Transactional
    public YardResponse createYard(YardRequest request) {
        Tehsil dbTehsil = tehsilRepository.findById(request.getTehsilId())
                .orElseThrow(() -> new ResourceNotFoundException("Tehsil with ID: " + request.getTehsilId() + " not found."));

        Yard yard = new Yard();
        yard.setYardName(request.getYardName());
        yard.setYardType(request.getYardType());
        yard.setTehsil(dbTehsil);

        applyGeometryToYard(yard, request.getBoundaryType(), request.getCenterLat(), request.getCenterLng(),
                request.getRadiusMeters(), request.getPolygonPath());

        try {
            return ResponseConversion.toYardResponse(yardRepository.saveAndFlush(yard));
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to add yard: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public YardResponse updateYard(UUID yardId, YardUpdateRequest request) {
        Yard dbYard = yardRepository.findById(yardId)
                .orElseThrow(() -> new ResourceNotFoundException("Yard with ID: " + yardId + " not found."));

        if (request.getYardName() != null) dbYard.setYardName(request.getYardName());
        if (request.getYardType() != null) dbYard.setYardType(request.getYardType());
        if (request.getStatus() != null) dbYard.setStatus(Status.valueOf(request.getStatus()));

        if (request.getBoundaryType() != null) {
            dbYard.setCenterPoint(null);
            dbYard.setRadiusMeters(null);
            dbYard.setBoundaryPolygon(null);

            applyGeometryToYard(dbYard, request.getBoundaryType(), request.getCenterLat(), request.getCenterLng(),
                    request.getRadiusMeters(), request.getPolygonPath());
        }

        try {
            return ResponseConversion.toYardResponse(yardRepository.saveAndFlush(dbYard));
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to update yard: " + e.getMessage());
        }
    }

    @Override
    public YardResponse getYardById(UUID yardId) {
        Yard dbYard = yardRepository.findById(yardId)
                .orElseThrow(() -> new ResourceNotFoundException("Yard with ID: " + yardId + " not found."));
        return ResponseConversion.toYardResponse(dbYard);
    }

    @Override
    public List<YardResponse> getAllYards() {
        return yardRepository.findAll().stream()
                .map(ResponseConversion::toYardResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<YardResponse> getAllYardsByTehsil(UUID tehsilId) {
        return yardRepository.findAllByTehsil_TehsilId(tehsilId).stream()
                .map(ResponseConversion::toYardResponse)
                .collect(Collectors.toList());
    }

    private void applyGeometryToYard(Yard yard, String boundaryType, Double lat, Double lng, Double radius, String path) {
        if ("RADIUS".equalsIgnoreCase(boundaryType)) {
            if (lat == null || lng == null || radius == null) {
                throw new IllegalArgumentException("Center coordinates and radius are required for RADIUS boundary.");
            }
            yard.setCenterPoint(SpatialUtils.toPoint(lat, lng));
            yard.setRadiusMeters(radius);
            yard.setStatus(Status.ACTIVE);

        } else if ("POLYGON".equalsIgnoreCase(boundaryType)) {
            if (path == null || path.isEmpty()) {
                throw new IllegalArgumentException("Encoded polygon path is required for POLYGON boundary.");
            }
            yard.setBoundaryPolygon(SpatialUtils.toPolygon(path));
            yard.setStatus(Status.ACTIVE);

        } else {
            throw new IllegalArgumentException("Boundary type must be exactly RADIUS or POLYGON");
        }
    }
}