package com.ivan_degtev.whatsappbotforneoderma.controller;

import com.ivan_degtev.whatsappbotforneoderma.service.impl.chatPush.ChatPushReceiveServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class WhatsAppController {
    private final ChatPushReceiveServiceImpl chatPushService;
    private final MessageService messageService;

    @Value("${ngrok.url}")
    private String ngrokUrl;
    @Value("${chatPush.api.key}")
    private String chatPushApiKey;

    public WhatsAppController(
            ChatPushReceiveServiceImpl chatPushService,
            MessageService messageService,
            @Value("${ngrok.url}") String ngrokUrl,
            @Value("${chatPush.api.key}") String chatPushApiKey
    ) {
        this.chatPushService = chatPushService;
        this.messageService = messageService;
        this.ngrokUrl = ngrokUrl;
        this.chatPushApiKey = chatPushApiKey;
    }

//    /**
//     * УТИЛИТНАЯ Ручка, настраивает коннект с chatpush через прокси ngrok для вотсапа. После первичного(!!) перехода по ней -
//     * сообщения с вотсапа будут поступать на следующую ручку /webhook,  как строка+мапа с хедерами
//     */
//    @PostMapping("/activate-webhook")
//    public Mono<String> activateWebhook() {
//        String webhookUrl = ngrokUrl + "/webhook";
//        List<String> eventTypes = List.of("whatsapp_incoming_msg");
//
//        return chatPushService.createWebhook(webhookUrl, eventTypes);
//    }

    /**
     * На эту ручку будут приходить все входщие и исходящие сообщения с вотсапа, переброшенные сюда через чатпуш -> ngrok
     * Нужно в дальнейшей логике корректно фильтровать и не обрабатывать через LLM исходящие сообшения
     */
    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload
    ) {
        log.info("зашёл в метод чекрез ngrock");
        chatPushService.getMessageFromWebhook(headers, payload);
    }


//    /**
//     * Тестовый метод для получения всех настроенных веб-хуков, не используется
//     */
//    @GetMapping("/filter-webhooks")
//    public Mono<Map<String, Object>> filterWebhooks() {
//        Mono<Map<String, Object>> allWebhooks =  chatPushService.getAllWebhooks();
//        log.info("ответ - все веб хуки {}", allWebhooks.toString());
//        return allWebhooks;
//    }
//
//    /**
//     * Утилитный эндпоинт для удаления вебхука по его ID.
//     *
//     * @param webhookId Уникальный идентификатор вебхука
//     * @return Сообщение об успешном удалении или ошибка
//     */
//    @DeleteMapping("/{id}")
//    public Mono<ResponseEntity<String>> deleteWebhook(@PathVariable("id") int webhookId) {
//        return chatPushService.deleteWebhook(webhookId)
//                .map(response -> ResponseEntity.ok("Webhook deleted successfully"))
//                .doOnError(error -> log.error("Failed to delete webhook", error))
//                .onErrorResume(error -> Mono.just(ResponseEntity.status(404).body("Webhook not found")));
//    }

}
