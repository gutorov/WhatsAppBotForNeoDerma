package com.ivan_degtev.whatsappbotforneoderma.service.impl.chatPush;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.model.enums.Direction;
import com.ivan_degtev.whatsappbotforneoderma.repository.MessageRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushServiceAdapter;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class ChatPushReceiveServiceImpl extends ChatPushServiceAdapter {

    @Value("${chatPush.api.key}")
    private String chatPushApiKey;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final MessageRepository messageRepository;

    private final Object lock = new Object();


    public ChatPushReceiveServiceImpl(
            @Value("${chatPush.api.key}") String chatPushApiKey,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            UserService userService,
            MessageRepository messageRepository
    ) {
        this.chatPushApiKey = chatPushApiKey;
        this.webClient = webClientBuilder
                .baseUrl("https://api.chatpush.ru/api/v1")
                .build();
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.messageRepository = messageRepository;
    }


    /**
     * основной метод взаимодействия(получения) сообщений из вотсапа по веб-хукам, при получении сообщения
     * отдаёт Mono<String> для дальнейшей работы внутри приложения.
     */
//    @Override
//    public Mono<String> createWebhook(String url, List<String> types) {
//        return webClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/webhooks")
//                        .queryParam("url", url)
//                        .queryParam("types[]", String.join("&types[]=", types))
//                        .build())
//                .header("Authorization", "Bearer " + chatPushApiKey)
//                .retrieve()
//                .bodyToMono(String.class);
//    }
    @Override
    public void getMessageFromWebhook(
            Map<String, String> headers,
            String payload
    ) {
        WebhookPayload webhookPayload = convertStringToWebhookPayload(payload).block();
        String chatPushMessageId = Objects.requireNonNull(webhookPayload).getPayload().getNewMessage().getMessage().getId();
        //уникальный айди для каждого сообщения из чат пуш + нгрок
        synchronized (lock) {
            if (
                    Objects.nonNull(webhookPayload) &&
                            webhookPayload.getPayload().getNewMessage().getDirection().equals(Direction.incoming) &&
                            !testForSingleWebhookProcessing(chatPushMessageId)
            ) {
                userService.addingUserWhenThereIsNone(webhookPayload);
            }
        }
    }

    /**
     * Утилитный метод проверяет есть ли уже в БД сообщний сообщения с тем же айди , что и присываемое снова.
     * Решает проблему множественной отправки сообщений через веб-хуки
     * @param chatPushMessageId
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean testForSingleWebhookProcessing(String chatPushMessageId) {
        return messageRepository.existsByChatPushMessageId(chatPushMessageId);
    }

    private Mono<WebhookPayload> convertStringToWebhookPayload(String stringPayload) {
        try {
            WebhookPayload request = objectMapper.readValue(stringPayload, WebhookPayload.class);
            return Mono.just(request);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to deserialize incoming request", e));
        }
    }
//
//    /**
//     * Тестовый метод, получает все активные веб-хуки
//     */
//    public Mono<Map<String, Object>> getAllWebhooks() {
//        return webClient.get()
//                .uri("https://api.chatpush.ru/api/v1/webhooks/")
//                .header("Authorization", "Bearer " + chatPushApiKey)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
//    }
}
