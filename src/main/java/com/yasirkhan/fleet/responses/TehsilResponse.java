package com.yasirkhan.fleet.responses;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TehsilResponse {
    private UUID tehsilId;
    private String tehsilName;

    // Optional: Returning the list of yards inside the Tehsil is incredibly
    // helpful for the frontend to build dropdown menus!
    private List<YardResponse> yards;
}