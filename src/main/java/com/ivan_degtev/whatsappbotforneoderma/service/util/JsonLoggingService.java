package com.ivan_degtev.whatsappbotforneoderma.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class JsonLoggingService {
    private final ObjectMapper objectMapper;

    public void info(String message, Object object) {
        try {
            String prettyJson = objectMapper.writeValueAsString(object);
            log.info("{}:\n{}", message, prettyJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON", e);
        }
    }
    public void error(String message, Object object) {
        try {
            String prettyJson = objectMapper.writeValueAsString(object);
            log.info("{}:\n{}", message, prettyJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON", e);
        }
    }
}
