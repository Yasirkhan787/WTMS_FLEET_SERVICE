package com.yasirkhan.fleet.responses;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TehsilWithYardsResponse {
    private UUID tehsilId;
    private String tehsilName;
    private List<YardResponse> tcpYards; // Contains all TCPs for this Tehsil
    private List<YardResponse> dumpYards; // Contains all Dump Sites for this Tehsil
}