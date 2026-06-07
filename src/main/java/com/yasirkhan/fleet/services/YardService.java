package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.requests.YardRequest;
import com.yasirkhan.fleet.requests.YardUpdateRequest;
import com.yasirkhan.fleet.responses.YardResponse;

import java.util.List;
import java.util.UUID;

public interface YardService {
    YardResponse createYard(YardRequest request);
    YardResponse updateYard(UUID yardId, YardUpdateRequest request);
    YardResponse getYardById(UUID yardId);
    List<YardResponse> getAllYards();

    // ADDED THIS
    List<YardResponse> getAllYardsByTehsil(UUID tehsilId);
}