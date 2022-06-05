package com.kwj.oauth.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .findAndRegisterModules();

    public static Optional<String> writeValue(Object value) {
        try {
            return Optional.ofNullable(objectMapper.writeValueAsString(value));
        } catch (Exception e) {
            return getEmptyWithLogError(value, e);
        }
    }

    public static <T> Optional<T> readValue(String value, Class<T> clazz) {
        try {
            return Optional.ofNullable(objectMapper.readValue(value, clazz));
        } catch (Exception e) {
            return getEmptyWithLogError(value, e);
        }
    }

    public static <T> Optional<T> readValue(String value, TypeReference<T> typeReference) {
        try {
            return Optional.ofNullable(objectMapper.readValue(value, typeReference));
        } catch (Exception e) {
            return getEmptyWithLogError(value, e);
        }
    }

    public static <T> Optional<T> convertValue(Object value, Class<T> clazz) {
        try {
            return Optional.ofNullable(objectMapper.convertValue(value, clazz));
        } catch (Exception e) {
            return getEmptyWithLogError(value, e);
        }
    }

    public static <T> Optional<T> convertValue(Object value, TypeReference<T> typeReference) {
        try {
            return Optional.ofNullable(objectMapper.convertValue(value, typeReference));
        } catch (Exception e) {
            return getEmptyWithLogError(value, e);
        }
    }

    private static <T> Optional<T> getEmptyWithLogError(Object value, Exception e) {
        log.error("(JsonUtils) {}", value, e);
        return Optional.empty();
    }

}
