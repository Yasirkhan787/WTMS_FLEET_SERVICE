package com.yasirkhan.fleet.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.dtos.DriverStatusChangedEventDto;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomDeserializer implements Deserializer<Object> {

    private final ObjectMapper objectMapper;

    public CustomDeserializer() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            if (data == null) return null;

            // 1. Read the JSON into a generic tree
            JsonNode node = objectMapper.readTree(data);

            // 2. Logic: Peek at fields to decide the class
            if (node.has("updateType") || !node.has("cnic")) {
                // If it lacks 'cnic' or has 'updateType', it's a status event
                return objectMapper.treeToValue(node, DriverStatusChangedEventDto.class);
            } else {
                // Otherwise, it's the full profile
                return objectMapper.treeToValue(node, DriverDto.class);
            }
        } catch (Exception e) {
            throw new SerializationException("Error deserializing message", e);
        }
    }
    // TODO:
    /*@Override
    public Object deserialize(String topic, Headers headers, byte[] data) {

        if (data == null) return null;

        try {

            String eventType = new String(
                    headers.lastHeader("eventType").value(),
                    StandardCharsets.UTF_8
            );

            if ("DRIVER_CREATED".equals(eventType)) {
                return objectMapper.readValue(data, DriverDto.class);
            }

            if ("DRIVER_STATUS_CHANGED".equals(eventType)) {
                return objectMapper.readValue(data, DriverStatusChangedEventDto.class);
            }

            throw new SerializationException("Unknown event type: " + eventType);

        } catch (Exception e) {
            throw new SerializationException("Error deserializing message", e);
        }
    }*/
}
