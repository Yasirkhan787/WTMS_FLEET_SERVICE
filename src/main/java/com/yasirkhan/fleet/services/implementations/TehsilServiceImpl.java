package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.exceptions.ResourceNotFoundException;
import com.yasirkhan.fleet.models.entities.Tehsil;
import com.yasirkhan.fleet.models.enums.YardType;
import com.yasirkhan.fleet.repositories.TehsilRepository;
import com.yasirkhan.fleet.requests.TehsilRequest;
import com.yasirkhan.fleet.responses.TehsilResponse;
import com.yasirkhan.fleet.responses.TehsilWithYardsResponse;
import com.yasirkhan.fleet.responses.YardResponse;
import com.yasirkhan.fleet.services.TehsilService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TehsilServiceImpl implements TehsilService {

    private final TehsilRepository tehsilRepository;

    public TehsilServiceImpl(TehsilRepository tehsilRepository) {
        this.tehsilRepository = tehsilRepository;
    }

    @Override
    @Transactional
    public TehsilResponse createTehsil(TehsilRequest request) {

        if (tehsilRepository.existsByTehsilNameIgnoreCase(request.getTehsilName())) {
            throw new IllegalArgumentException("A Tehsil with the name '" + request.getTehsilName() + "' already exists.");
        }

        Tehsil tehsil = new Tehsil();
        tehsil.setTehsilName(request.getTehsilName());

        try {
            Tehsil savedTehsil = tehsilRepository.saveAndFlush(tehsil);
            return ResponseConversion.toTehsilResponse(savedTehsil);
        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to add Tehsil: " + e.getMessage());
        }
    }

    @Override
    public List<TehsilResponse> getAllTehsils() {
        return tehsilRepository.findAll().stream()
                .map(ResponseConversion::toTehsilResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TehsilResponse getTehsilById(UUID tehsilId) {
        Tehsil dbTehsil = tehsilRepository.findById(tehsilId)
                .orElseThrow(() -> new ResourceNotFoundException("Tehsil with ID: " + tehsilId + " not found."));
        return ResponseConversion.toTehsilResponse(dbTehsil);
    }

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
}