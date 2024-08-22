package com.ivan_degtev.whatsappbotforneoderma.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.SendingMessageResponse;
import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushService;
import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class ChatPushSenderServiceImpl extends ChatPushServiceAdapter {

    @Value("${ngrok.url}")
    private String ngrokUrl;
    @Value("${chatpush.api.key}")
    private String chatpushApiKey;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;


    public ChatPushSenderServiceImpl (
            @Value("${ngrok.url}") String ngrokUrl,
            @Value("${chatpush.api.key}") String chatpushApiKey,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper
    ) {
        this.ngrokUrl = ngrokUrl;
        this.chatpushApiKey = chatpushApiKey;
        this.webClient = webClientBuilder
                .baseUrl("https://api.chatpush.ru/api/v1")
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * Метод для возврата ответа юзеру, принимает тело ответа(строку) и телефон(строку).
     * Сериализует ответ в WebhookPayloadOutgoing, делает сервисные сохранения в БД и возвращает ответ через ручку веб-хуков
     */
    @Override
    public Mono<SendingMessageResponse> sendMessage(String text, String phone) {
        log.info("Зашёл в метод отправки сообщений на чат пуш - текст {}, телефон {}",
                text, phone);
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/delivery")
                        .queryParam("text", text)
                        .queryParam("phone", phone)
                        .build())
                .header("Authorization", "Bearer " + chatpushApiKey)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(responseString -> {
                    log.info("Received response string: {}", responseString);
                })
                .flatMap(this::convertStringToWebhookPayloadOutgoing);
    }

    private Mono<SendingMessageResponse> convertStringToWebhookPayloadOutgoing(String stringPayload) {
        try {
            SendingMessageResponse responce = objectMapper.readValue(stringPayload, SendingMessageResponse.class);
            return Mono.just(responce);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to deserialize outgoing request", e));
        }
    }

    @Override
    public Mono<String> createWebhook(String url, List<String> types) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/webhooks")
                        .queryParam("url", url)
                        .queryParam("types[]", String.join("&types[]=", types))
                        .build())
                .header("Authorization", "Bearer " + chatpushApiKey)
                .retrieve()
                .bodyToMono(String.class);

    }
}
