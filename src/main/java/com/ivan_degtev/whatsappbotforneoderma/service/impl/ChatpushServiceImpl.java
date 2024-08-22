package com.ivan_degtev.whatsappbotforneoderma.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.dto.SendingMessageResponse;
import com.ivan_degtev.whatsappbotforneoderma.model.enums.Direction;
import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushService;
import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushServiceAdapter;
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
public class ChatpushServiceImpl extends ChatPushServiceAdapter {

    @Value("${chatpush.api.key}")
    private String chatpushApiKey;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    public ChatpushServiceImpl(
            @Value("${chatpush.api.key}") String chatpushApiKey,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            UserService userService
    ) {
        this.chatpushApiKey = chatpushApiKey;
        this.webClient = webClientBuilder
                .baseUrl("https://api.chatpush.ru/api/v1")
                .build();
        this.objectMapper = objectMapper;
        this.userService = userService;
    }


    /**
     * основной метод взаимодействия(получения) сообщений из вотсапа по веб-хукам, при получении сообщения
     * отдаёт Mono<String> для дальнейшей работы внутри приложения.
     */
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

    @Override
    public void getMessageFromWebhook(
            Map<String, String> headers,
            String payload
    ) {
        WebhookPayload webhookPayload = convertStringToWebhookPayload(payload).block();
        if (Objects.nonNull(webhookPayload) &&
        webhookPayload.getPayload().getNewMessage().getDirection().equals(Direction.incoming)) {
            userService.addingUserWhenThereIsNone(webhookPayload);
        }
    }

//    /**
//     * Метод для возврата ответа юзеру, принимает тело ответа(строку) и телефон(строку).
//     * Сериализует ответ в WebhookPayloadOutgoing, делает сервисные сохранения в БД и возвращает ответ через ручку веб-хуков
//     */
//    public Mono<SendingMessageResponse> sendMessage(String text, String phone) {
//        return webClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/delivery")
//                        .queryParam("text", text)
//                        .queryParam("phone", phone)
//                        .build())
//                .header("Authorization", "Bearer " + chatpushApiKey)
//                .retrieve()
//                .bodyToMono(String.class)
//                .doOnNext(responseString -> {
//                    log.info("Received response string: {}", responseString);
//                })
//                .flatMap(this::convertStringToWebhookPayloadOutgoing);
//    }

    private Mono<WebhookPayload> convertStringToWebhookPayload(String stringPayload) {
        try {
            WebhookPayload request = objectMapper.readValue(stringPayload, WebhookPayload.class);
            return Mono.just(request);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to deserialize incoming request", e));
        }
    }
//    private Mono<SendingMessageResponse> convertStringToWebhookPayloadOutgoing(String stringPayload) {
//        try {
//            SendingMessageResponse responce = objectMapper.readValue(stringPayload, SendingMessageResponse.class);
//            return Mono.just(responce);
//        } catch (Exception e) {
//            return Mono.error(new RuntimeException("Failed to deserialize outgoing request", e));
//        }
//    }

    /**
     * Тестовый метод, получает все активные веб-хуки
     */
    public Mono<Map<String, Object>> getAllWebhooks() {
        return webClient.get()
                .uri("https://api.chatpush.ru/api/v1/webhooks/")
                .header("Authorization", "Bearer " + chatpushApiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}
