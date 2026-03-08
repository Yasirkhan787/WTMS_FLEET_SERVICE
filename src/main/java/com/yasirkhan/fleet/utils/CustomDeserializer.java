package com.yasirkhan.fleet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yasirkhan.fleet.models.dtos.DriverDto;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class CustomDeserializer implements Deserializer<DriverDto> {

    private final ObjectMapper objectMapper;

    public CustomDeserializer() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public DriverDto deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                throw new RuntimeException("Error deserializing JSON message from topic: " + topic);
            }
            return objectMapper.readValue(new String(data, "UTF-8"), DriverDto.class);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing JSON message from topic: " + topic, e);
        }
    }
}
