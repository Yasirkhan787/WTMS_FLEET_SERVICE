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
    private String status;
}