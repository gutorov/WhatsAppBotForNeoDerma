package com.ivan_degtev.whatsappbotforneoderma.controller.whatsapp;

import com.ivan_degtev.whatsappbotforneoderma.service.impl.chatPush.ChatPushReceiveServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


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
    public ResponseEntity<String> handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload
    ) {
        log.info("зашёл в метод чекрез ngrock");
        CompletableFuture.runAsync(() -> chatPushService.getMessageFromWebhook(headers, payload));
        return ResponseEntity.ok("Сообщение через веб-хук успешно получено!");
    }

}
