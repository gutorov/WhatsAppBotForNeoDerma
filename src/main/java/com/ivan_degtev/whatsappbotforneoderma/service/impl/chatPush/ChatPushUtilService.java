package com.ivan_degtev.whatsappbotforneoderma.service.impl.chatPush;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.model.enums.Direction;
import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushServiceAdapter;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class ChatPushUtilService extends ChatPushServiceAdapter {
    @Value("${chatPush.api.key}")
    private String chatPushApiKey;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final JsonLoggingService jsonLogging;

    public ChatPushUtilService(
            @Value("${chatPush.api.key}") String chatPushApiKey,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            JsonLoggingService jsonLogging
    ) {
        this.chatPushApiKey = chatPushApiKey;
        // сюда вставил новые данные  - новый урл по чатпушу, неделю назад он был другой - остальные сервис
        // по приёмы и отправке сообщений - пока со старым урлом
        this.webClient = webClientBuilder
//                .baseUrl("https://api.pushsms.ru/developer/v1")
                .baseUrl("https://api.chatpush.ru/api/v1")
                .build();
        this.objectMapper = objectMapper;
        this.jsonLogging = jsonLogging;
    }

    @Override
    public Mono<String> createWebhook(String url, List<String> types) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/webhooks")
                        .queryParam("url", url)
                        .queryParam("types[]", String.join("&types[]=", types))
                        .build())
                .header("Authorization", "Bearer " + chatPushApiKey)
                .retrieve()
                .bodyToMono(String.class);
    }



    private Mono<WebhookPayload> convertStringToWebhookPayload(String stringPayload) {
        try {
            WebhookPayload request = objectMapper.readValue(stringPayload, WebhookPayload.class);
            return Mono.just(request);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to deserialize incoming request", e));
        }
    }

    /**
     * Метод для получения всех вебхуков.
     *
     * @return Mono с мапой, содержащей данные о вебхуках
     */
    public Mono<Map<String, Object>> getAllWebhooks() {
        return webClient.get()
                .uri("/webhooks")
                .header("Authorization",  chatPushApiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnSuccess(response -> log.info("Successfully retrieved webhooks: {}", response))
                .doOnError(error -> log.error("Failed to retrieve webhooks", error));
    }

    /**
     * Метод для удаления вебхука по его ID.
     *
     * @param webhookId Уникальный идентификатор вебхука
     * @return Mono с ответом от сервиса
     */
    public Mono<String> deleteWebhook(int webhookId) {
        return webClient.delete()
                .uri("/webhooks/{id}", webhookId)
                .header("Authorization", "Bearer " + chatPushApiKey)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> jsonLogging.info("Successfully deleted webhook with ID: {}", webhookId))
                .doOnError(error -> jsonLogging.error("Failed to delete webhook with ID: {}", error));
    }
}
