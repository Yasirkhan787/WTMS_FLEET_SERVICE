package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.requests.TehsilRequest;
import com.yasirkhan.fleet.responses.TehsilResponse;
import com.yasirkhan.fleet.responses.TehsilWithYardsResponse;

import java.util.List;
import java.util.UUID;

public interface TehsilService {

    TehsilResponse createTehsil(TehsilRequest request);

    List<TehsilResponse> getAllTehsils();

    TehsilResponse getTehsilById(UUID tehsilId);

    List<TehsilWithYardsResponse> getAllTehsilsWithYards();
}