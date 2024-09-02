package com.ivan_degtev.whatsappbotforneoderma.controller.whatsapp;

import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushService;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/webhook")
@RestController
@Slf4j
public class WhatsAppUtilController {
    @Value("${ngrok.url}")
    private String ngrokUrl;
    @Value("${chatPush.api.key}")
    private String chatPushApiKey;
    private final ChatPushService chatPushService;
    private final JsonLoggingService jsonLogging;

    public WhatsAppUtilController(
            @Value("${ngrok.url}") String ngrokUrl,
            @Value("${chatPush.api.key}") String chatPushApiKey,
            @Qualifier("chatPushUtilService") ChatPushService chatPushService,
            JsonLoggingService jsonLogging
    ) {
        this.ngrokUrl = ngrokUrl;
        this.chatPushApiKey = chatPushApiKey;
        this.chatPushService = chatPushService;
        this.jsonLogging = jsonLogging;
    }


    /**
     * УТИЛИТНАЯ Ручка, настраивает коннект с chatpush через прокси ngrok для вотсапа. После первичного(!!) перехода по ней -
     * сообщения с вотсапа будут поступать на следующую ручку /webhook,  как строка+мапа с хедерами
     */
    @PostMapping("/activate-webhook")
    public Mono<String> activateWebhook() {
        String webhookUrl = ngrokUrl + "/webhook";
        List<String> eventTypes = List.of("whatsapp_incoming_msg");

        return chatPushService.createWebhook(webhookUrl, eventTypes);
    }

    /**
     * Тестовый метод для получения всех настроенных веб-хуков, не используется
     */
    @GetMapping("/filter-webhooks")
    public Mono<Map<String, Object>> filterWebhooks() {
        Mono<Map<String, Object>> allWebhooks =  chatPushService.getAllWebhooks();
        log.info("ответ - все веб хуки {}", allWebhooks.toString());
        return allWebhooks;
    }

    /**
     * Утилитный эндпоинт для удаления вебхука по его ID.
     *
     * @param webhookId Уникальный идентификатор вебхука
     * @return Сообщение об успешном удалении или ошибка
     */
    @DeleteMapping("/delete-webhook/{id}")
    public Mono<ResponseEntity<String>> deleteWebhook(@PathVariable("id") int webhookId) {
        return chatPushService.deleteWebhook(webhookId)
                .map(response -> ResponseEntity.ok("Webhook deleted successfully"))
                .doOnError(error -> jsonLogging.error("Failed to delete webhook", error))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(404).body("Webhook not found")));
    }
}
