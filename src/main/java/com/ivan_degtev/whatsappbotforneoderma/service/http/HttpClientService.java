package com.ivan_degtev.whatsappbotforneoderma.service.http;

import dev.langchain4j.service.V;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpClientService {

    private final RestTemplate restTemplate;

    @Value("${amocrm.api.url}")
    private String amocrmApiUrl;

    @Value("${amocrm.token}")
    private String accessToken;

    public String buildUrl(String endpoint) {
        return amocrmApiUrl + endpoint;
    }

    public <T> ResponseEntity<T> postWithAuth(String url, Object requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        } catch (RestClientException e) {
            log.error("Failed to make HTTP POST request to URL: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public <T> ResponseEntity<T> getWithAuth(String url, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        } catch (RestClientException e) {
            log.error("Failed to make HTTP GET request to URL: {}", url, e);
            return ResponseEntity.status(500).build();
        }
    }

    public static boolean isSuccessfulResponse(ResponseEntity<?> response) {
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
    }
}

