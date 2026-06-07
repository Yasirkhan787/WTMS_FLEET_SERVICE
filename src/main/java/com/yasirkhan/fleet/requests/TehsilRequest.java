package com.yasirkhan.fleet.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TehsilRequest {
    @NotBlank(message = "Tehsil name is required")
    private String tehsilName; // e.g., "Potohar Town"
}