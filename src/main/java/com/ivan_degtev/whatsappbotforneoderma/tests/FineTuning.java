package com.ivan_degtev.whatsappbotforneoderma.tests;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;

import java.util.HashMap;
import java.util.Map;

public class FineTuning {
    private static final String OPENAI_API_KEY = "ваш_ключ_доступа_к_OpenAI";
    private static final String OPENAI_UPLOAD_URL = "https://api.openai.com/v1/files";
    private static final String OPENAI_FINE_TUNING_URL = "https://api.openai.com/v1/fine-tuning/jobs";

    private final RestTemplate restTemplate;

    public FineTuning(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadTrainingFile(String filePath) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(OPENAI_API_KEY);

        FileSystemResource fileResource = new FileSystemResource(filePath);

        Map<String, Object> body = new HashMap<>();
        body.put("file", fileResource);
        body.put("purpose", "fine-tune");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                OPENAI_UPLOAD_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // Получаем ID файла из ответа
        String fileId = (String) response.getBody().get("id");
        System.out.println("FileID: " + fileId);
        return fileId;
    }

    public void createFineTuningJob(String fileId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("training_file", fileId);
        body.put("suffix", "testTuning");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_FINE_TUNING_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Fine-tuning job created: " + response.getBody());
            } else {
                System.err.println("Failed to create fine-tuning job. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
