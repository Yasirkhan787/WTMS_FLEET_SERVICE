package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.exceptions.ResourceNotFoundException;
import com.yasirkhan.fleet.models.dtos.DailyGoalResponseEventDto;
import com.yasirkhan.fleet.models.dtos.RouteResponseEventDto;
import com.yasirkhan.fleet.models.entities.DailyGoal;
import com.yasirkhan.fleet.models.entities.Route;
import com.yasirkhan.fleet.models.entities.Tehsil;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.EventType;
import com.yasirkhan.fleet.repositories.DailyGoalRepository;
import com.yasirkhan.fleet.repositories.TehsilRepository;
import com.yasirkhan.fleet.requests.DailyGoalRequest;
import com.yasirkhan.fleet.responses.DailyGoalResponse;
import com.yasirkhan.fleet.responses.RouteResponse;
import com.yasirkhan.fleet.services.DailyGoalService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DailyGoalServiceImpl implements DailyGoalService {

    private final DailyGoalRepository dailyGoalRepository;
    private final TehsilRepository tehsilRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    public DailyGoalServiceImpl(DailyGoalRepository dailyGoalRepository, TehsilRepository tehsilRepository, ApplicationEventPublisher eventPublisher, RedisTemplate<String, Object> redisTemplate) {
        this.dailyGoalRepository = dailyGoalRepository;
        this.tehsilRepository = tehsilRepository;
        this.eventPublisher = eventPublisher;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public DailyGoalResponse assignGoal(DailyGoalRequest request) {

        // Validate Tehsil exists
        Tehsil dbTehsil = tehsilRepository.findById(request.getTehsilId())
                .orElseThrow(() -> new ResourceNotFoundException("Tehsil with ID: " + request.getTehsilId() + " not found."));

        // Check for existing goal for this specific date and territory
        Optional<DailyGoal> existingGoal = dailyGoalRepository.findByTehsil_TehsilIdAndTargetDate(request.getTehsilId(), request.getTargetDate());

        // Get Admin ID from Security Context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized: No valid session found.");
        }
        String adminId = (String) auth.getPrincipal();

        DailyGoal goalToSave = existingGoal.orElseGet(DailyGoal::new);

        goalToSave.setTehsil(dbTehsil);
        goalToSave.setTargetDate(request.getTargetDate());
        goalToSave.setTargetTonnage(request.getTargetTonnage());
        goalToSave.setAssignedBy(adminId);

        try {
            DailyGoal savedGoal = dailyGoalRepository.saveAndFlush(goalToSave);

            syncGoalToRedis(savedGoal);

            DailyGoalResponse response = ResponseConversion.toDailyGoalResponse(savedGoal);

            publishGoalEvent(EventType.CREATE, EventStatus.SUCCESS, response);

            return response;
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to save Daily Goal: " + e.getMessage());
        }
    }

    @Override
    public List<DailyGoalResponse> getAllGoalsForTehsil(UUID tehsilId) {
        return dailyGoalRepository.findAllByTehsil_TehsilId(tehsilId).stream()
                .map(ResponseConversion::toDailyGoalResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyGoalResponse> getAllGoals() {
        return dailyGoalRepository.findAll().stream()
                .map(ResponseConversion::toDailyGoalResponse)
                .collect(Collectors.toList());
    }

    // Helper Methods
    @Override
    public Double getTargetTonnage(UUID tehsilId, LocalDate date) {
        return dailyGoalRepository.findByTehsil_TehsilIdAndTargetDate(tehsilId, date)
                .map(DailyGoal::getTargetTonnage)
                .orElse(0.0);
    }

    private void syncGoalToRedis(DailyGoal goal) {

        String redisKey = "wtms:goal:" + goal.getGoalId().toString();
        Map<String, Object> data = new HashMap<>();
        data.put("tehsilId", goal.getTehsil().getTehsilId());
        data.put("targetDate", goal.getTargetDate());
        data.put("targetTonnage", goal.getTargetTonnage());
        data.put("assignedBy", goal.getAssignedBy());

        redisTemplate.opsForHash().putAll(redisKey, data);
    }

    private void publishGoalEvent(EventType type, EventStatus status, DailyGoalResponse response) {

        DailyGoalResponseEventDto eventDto = DailyGoalResponseEventDto.builder()
                .type(type)
                .eventTypeStatus(status)
                .goalData(response)
                .build();

        eventPublisher.publishEvent(eventDto);
}
}