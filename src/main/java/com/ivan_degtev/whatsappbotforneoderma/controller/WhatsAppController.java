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

}
