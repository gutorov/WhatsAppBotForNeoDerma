package com.ivan_degtev.whatsappbotforneoderma.controller;

import com.ivan_degtev.whatsappbotforneoderma.dto.SendingMessageResponse;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.ChatpushServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class WhatsAppController {
    private final ChatpushServiceImpl chatpushService;
    private final MessageService messageService;

    @Value("${ngrok.url}")
    private String ngrokUrl;
    @Value("${chatpush.api.key}")
    private String chatpushApiKey;

    public WhatsAppController(
            ChatpushServiceImpl chatpushService,
            MessageService messageService
    ) {
        this.chatpushService = chatpushService;
        this.messageService = messageService;
    }

    /**
     * УТИЛИТНАЯ Ручка, настраивает коннект с chatpush через прокси ngrok для вотсапа. После первичного(!!) перехода по ней -
     * сообщения с вотсапа будут поступать на следующую ручку /webhook,  как строка+мапа с хедерами
     */
    @PostMapping("/activate-webhook")
    public Mono<String> activateWebhook() {
        String webhookUrl = ngrokUrl + "/webhook";
        List<String> eventTypes = List.of("whatsapp_incoming_msg");

        return chatpushService.createWebhook(webhookUrl, eventTypes);
    }

    /**
     * На эту ручку будут приходить все входщие сообщения с вотсапа, переброшенные сюда через чатпуш -> ngrok
     * @param headers
     * @param payload
     */
    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload
    ) {
        log.info("зашёл в метод чекрез ngrock");
        chatpushService.getMessageFromWebhook(headers, payload);
    }

    @PostMapping("/send-message")
    public Mono<SendingMessageResponse> sendMessage(
            @RequestParam String text,
            @RequestParam String phone
    ) {
        return chatpushService.sendMessage(text, phone)
                .doOnSuccess(response -> {
                    System.out.println("Message sent successfully: " + response.toString());
                })
                .doOnError(error -> System.err.println("Failed to send message: " + error.getMessage()));
    }


    /**
     * Тестовый метод для получения всех настроенных веб-хуков, не используется
     */
    @GetMapping("/filter-webhooks")
    public Mono<Map<String, Object>> filterWebhooks() {
        Mono<Map<String, Object>> allWebhooks =  chatpushService.getAllWebhooks();
        log.info("ответ - все веб хуки {}", allWebhooks.toString());
        return allWebhooks;
    }
}
