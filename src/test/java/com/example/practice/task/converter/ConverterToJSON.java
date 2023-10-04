package com.example.practice.task.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverterToJSON {
    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
