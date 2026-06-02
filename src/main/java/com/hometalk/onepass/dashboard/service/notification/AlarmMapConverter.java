package com.hometalk.onepass.dashboard.service.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Converter
public class AlarmMapConverter implements AttributeConverter<Map<AlarmCategory, Set<Long>>, String>{

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 엔티티 -> DB (Map을 JSON 문자열로 변환)
    @Override
    public String convertToDatabaseColumn(Map<AlarmCategory, Set<Long>> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 변환 중 에러 발생", e);
        }
    }

    // DB -> 엔티티 (JSON 문자열을 다시 Map으로 변환)
    @Override
    public Map<AlarmCategory, Set<Long>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<AlarmCategory, Set<Long>>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON 읽기 중 에러 발생", e);
        }
    }
}
