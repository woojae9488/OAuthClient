package com.example.oauth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public <T> Optional<T> readValue(String content, Class<T> valueType) {
        try {
            return Optional.of(MAPPER.readValue(content, valueType));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public <T> Optional<T> readValue(String content, TypeReference<T> valueTypeRef) {
        try {
            return Optional.of(MAPPER.readValue(content, valueTypeRef));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public Optional<String> writeValue(Object value) {
        try {
            return Optional.of(MAPPER.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public <T> Optional<T> convertValue(Object fromValue, Class<T> toValueType) {
        return Optional.of(MAPPER.convertValue(fromValue, toValueType));
    }

    public <T> Optional<T> convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return Optional.of(MAPPER.convertValue(fromValue, toValueTypeRef));
    }

}
