package com.ivan_degtev.whatsappbotforneoderma.service.ai;

import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;


@Service
@Slf4j
public class FineTuningService {

    @Value("${open.ai.token}")
    private String openAiApiKey;
    private final RestTemplate restTemplate;
    private final JsonLoggingService jsonLogging;

    private static final String OPENAI_UPLOAD_URL = "https://api.openai.com/v1/files";
    private static final String OPENAI_FINE_TUNING_URL = "https://api.openai.com/v1/fine_tuning/jobs";


    public FineTuningService(
            @Value("${open.ai.token}") String openAiApiKey,
            RestTemplate restTemplate,
            JsonLoggingService jsonLogging
    ) {
        this.openAiApiKey = openAiApiKey;
        this.restTemplate = restTemplate;
        this.jsonLogging = jsonLogging;
    }

    /**
     * Сделать запрос на open ai для загрузки файла с данными для тонкой настройки модели
     * Далее - использоватьь полученный в ответе id для обучения новой модели по загруженным правилам.
     */
    public String uploadTrainingFile(String filePath) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(openAiApiKey);

        FileSystemResource fileResource = new FileSystemResource(filePath);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("purpose", "fine-tune");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_UPLOAD_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String fileId = (String) response.getBody().get("id");
                System.out.println("FileID: " + fileId);
                return fileId;
            } else {
                throw new RuntimeException("Failed to upload file. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("HTTP error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }



    public ResponseEntity<Map> createFineTuningJob(String fileId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        // Создание запроса для fine-tuning
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
                jsonLogging.info("Fine-tuning job created {}", response.getBody());
                return response;
            } else {
                throw new RuntimeException("Failed to create fine-tuning job. Status code " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("HTTP error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Map> getStatusFineTuningJob(String fineTuningJobId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String url = OPENAI_FINE_TUNING_URL + "/" + fineTuningJobId;

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response;
            } else {
                throw new RuntimeException("Failed to get fine-tuning job status. Status code " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("HTTP error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }
}


