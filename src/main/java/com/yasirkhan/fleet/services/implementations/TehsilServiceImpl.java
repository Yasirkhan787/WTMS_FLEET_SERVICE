package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.exceptions.ResourceNotFoundException;
import com.yasirkhan.fleet.models.dtos.RouteResponseEventDto;
import com.yasirkhan.fleet.models.dtos.TehsilResponseEventDto;
import com.yasirkhan.fleet.models.entities.Route;
import com.yasirkhan.fleet.models.entities.Tehsil;
import com.yasirkhan.fleet.models.entities.Vehicle;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.EventType;
import com.yasirkhan.fleet.models.enums.Status;
import com.yasirkhan.fleet.models.enums.YardType;
import com.yasirkhan.fleet.repositories.TehsilRepository;
import com.yasirkhan.fleet.requests.TehsilRequest;
import com.yasirkhan.fleet.responses.*;
import com.yasirkhan.fleet.services.TehsilService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import com.yasirkhan.fleet.utils.SpatialUtils;
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
public class TehsilServiceImpl implements TehsilService {

    private final TehsilRepository tehsilRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public TehsilServiceImpl(TehsilRepository tehsilRepository, RedisTemplate<String, Object> redisTemplate, ApplicationEventPublisher eventPublisher) {
        this.tehsilRepository = tehsilRepository;
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }

    // Create Tehsil
    @Override
    @Transactional
    public TehsilResponse createTehsil(TehsilRequest request) {

        if (tehsilRepository.existsByTehsilNameIgnoreCase(request.getTehsilName())) {
            throw new IllegalArgumentException("A Tehsil with the name '" + request.getTehsilName() + "' already exists.");
        }

        Tehsil tehsil = new Tehsil();
        tehsil.setTehsilName(request.getTehsilName());
        tehsil.setStatus(Status.ACTIVE);

        try {
            Tehsil savedTehsil = tehsilRepository.saveAndFlush(tehsil);

            TehsilResponse response = ResponseConversion.toTehsilResponse(savedTehsil);

            syncTehsilToRedis(tehsil);

            publishTehsilEvent(EventType.CREATE, EventStatus.SUCCESS,response);

            return response;
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to add Tehsil: " + e.getMessage());
        }
    }

    // Update Tehsil
    @Override
    public void updateTehsil(UUID tehsilId, TehsilRequest request) {

        Tehsil dbTehsil = tehsilRepository.findById(tehsilId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tehsil with Tehsil ID: " + tehsilId + " not found."));

        if (request.getTehsilName() != null) dbTehsil.setTehsilName(request.getTehsilName());

        try {
            Tehsil updatedTehsil = tehsilRepository.saveAndFlush(dbTehsil);

            syncTehsilToRedis(updatedTehsil);

            TehsilResponse response = ResponseConversion.toTehsilResponse(updatedTehsil);

            publishTehsilEvent(EventType.UPDATE, EventStatus.SUCCESS, response);

        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to update vehicle: " + e.getMessage());
        }
    }

    // Block Tehsil
    @Override
    @Transactional
    public void blockTehsil(UUID tehsilId, Boolean blockStatus) {

        Tehsil dbTehsil = tehsilRepository.findById(tehsilId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tehsil with Tehsil ID: " + tehsilId + " not found."));

        dbTehsil.setStatus(blockStatus ? Status.BLOCKED : Status.ACTIVE);

        try {
            Tehsil updatedTehsil = tehsilRepository.saveAndFlush(dbTehsil);

            syncTehsilToRedis(updatedTehsil);

            TehsilResponse response = ResponseConversion.toTehsilResponse(updatedTehsil);

            publishTehsilEvent(EventType.UPDATE, EventStatus.SUCCESS, response);
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to block Tehsil: " + e.getMessage());
        }
    }

    // Get All Tehsils
    @Override
    public List<TehsilResponse> getAllTehsils() {
        return tehsilRepository.findAll().stream()
                .map(ResponseConversion::toTehsilResponse)
                .collect(Collectors.toList());
    }

    // Get Tehsil By Id
    @Override
    public TehsilResponse getTehsilById(UUID tehsilId) {
        Tehsil dbTehsil = tehsilRepository.findById(tehsilId)
                .orElseThrow(() -> new ResourceNotFoundException("Tehsil with ID: " + tehsilId + " not found."));
        return ResponseConversion.toTehsilResponse(dbTehsil);
    }

    // Get All Tehsils With Yards
    @Override
    public List<TehsilWithYardsResponse> getAllTehsilsWithYards() {
        return tehsilRepository.findAll().stream()
                .map(tehsil -> {
                    List<YardResponse> tcpList = tehsil.getYards().stream()
                            .filter(y -> YardType.COLLECTION_POINT.equals(y.getYardType()))
                            .map(ResponseConversion::toYardResponse)
                            .collect(Collectors.toList());

                    List<YardResponse> dumpList = tehsil.getYards().stream()
                            .filter(y -> YardType.DUMP_SITE.equals(y.getYardType()))
                            .map(ResponseConversion::toYardResponse)
                            .collect(Collectors.toList());

                    return TehsilWithYardsResponse.builder()
                            .tehsilId(tehsil.getTehsilId())
                            .tehsilName(tehsil.getTehsilName())
                            .tcpYards(tcpList)
                            .dumpYards(dumpList)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Helper methods
    private void syncTehsilToRedis(Tehsil tehsil) {
        if (tehsil == null || tehsil.getTehsilId() == null) {
            return;
        }
        String redisKey = "wtms:tehsils:" + tehsil.getTehsilId();
        Map<String, String> data = new HashMap<>();
        data.put("tehsilName", tehsil.getTehsilName());
        redisTemplate.opsForHash().putAll(redisKey, data);
    }

    private void publishTehsilEvent(EventType type, EventStatus status, TehsilResponse response) {
        TehsilResponseEventDto eventDto = TehsilResponseEventDto.builder()
                .type(type)
                .eventTypeStatus(status)
                .tehsilData(response)
                .build();

        eventPublisher.publishEvent(eventDto);
    }
}