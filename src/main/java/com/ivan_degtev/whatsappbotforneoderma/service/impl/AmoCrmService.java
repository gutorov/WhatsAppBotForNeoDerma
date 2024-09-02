package com.ivan_degtev.whatsappbotforneoderma.service.impl;

import com.ivan_degtev.whatsappbotforneoderma.dto.amoCrm.LeadDto;
import com.ivan_degtev.whatsappbotforneoderma.service.http.HttpClientService;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmoCrmService {

    private final HttpClientService httpClientService;
    private final JsonLoggingService jsonLogging;

    @Value("${amocrm.api.url}")
    private String apiUrl;
    private static final String LEADS_URL = "/leads";

    public ResponseEntity<String> getAllLeads(int page, int limit) {
        String url = String.format("%s/leads?page=%d&limit=%d", apiUrl, page, limit);

        ResponseEntity<String> response = httpClientService.getWithAuth(
                url, String.class);
        if (HttpClientService.isSuccessfulResponse(response)) {
            jsonLogging.info("Получил ответ от амосрм по всем лидам {}", response);
            return response;
        } else {
            log.error("Failed to retrieve leads from AmoCRM");
            throw new RuntimeException("Не удалось получить сделки с AmoCRM");
        }
    }

    public ResponseEntity<String> getAllPipelines() {
        ResponseEntity<String> response = httpClientService.getWithAuth(apiUrl + "/leads/pipelines", String.class);

        if (HttpClientService.isSuccessfulResponse(response)) {
            jsonLogging.info("Получил ответ от амосрм по всем лидам {}", response);
            return response;
        } else {
            log.error("Failed to retrieve pipelines from AmoCRM");
            throw new RuntimeException("Не удалось получить воронки с AmoCRM");
        }
    }

    public String getCustomFieldsForLeads() {
        String url = httpClientService.buildUrl("/leads/custom_fields");

        ResponseEntity<String> response = httpClientService.getWithAuth(url, String.class);

        if (HttpClientService.isSuccessfulResponse(response)) {
            jsonLogging.info("Получил ответ от амосрм по всем кастомным полям {}", response);
            return response.getBody();
        } else {
            throw new RuntimeException("Не удалось получить поля сделок из AmoCRM");
        }
    }

    public String addLead(LeadDto leadDto) {
        String url = httpClientService.buildUrl(LEADS_URL);

        ResponseEntity<String> response = httpClientService.postWithAuth(url, List.of(leadDto), String.class);

        if (HttpClientService.isSuccessfulResponse(response)) {
            log.info("Лид успешно добавлен в AmoCRM: {}", response.getBody());
            return response.getBody();
        } else {
            log.error("Не удалось добавить лид в AmoCRM");
            throw new RuntimeException("Ошибка при добавлении лида в AmoCRM");
        }
    }

}

