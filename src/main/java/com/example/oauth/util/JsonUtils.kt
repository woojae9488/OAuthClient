package com.example.oauth.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

class JsonUtils private constructor() {

    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private val MAPPER = ObjectMapper()

        fun <T> readValue(content: String, valueType: Class<T>): T? {
            return try {
                MAPPER.readValue(content, valueType)
            } catch (e: JsonProcessingException) {
                null
            }
        }

        fun <T> readValue(content: String, valueTypeRef: TypeReference<T>): T? {
            return try {
                MAPPER.readValue(content, valueTypeRef)
            } catch (e: JsonProcessingException) {
                null
            }
        }

        fun writeValue(value: Any): String? {
            return try {
                MAPPER.writeValueAsString(value)
            } catch (e: JsonProcessingException) {
                null
            }
        }

        fun <T> convertValue(fromValue: Any, toValueType: Class<T>): T {
            return MAPPER.convertValue(fromValue, toValueType)
        }

        fun <T> convertValue(fromValue: Any, toValueTypeRef: TypeReference<T>): T {
            return MAPPER.convertValue(fromValue, toValueTypeRef)
        }
    }

}