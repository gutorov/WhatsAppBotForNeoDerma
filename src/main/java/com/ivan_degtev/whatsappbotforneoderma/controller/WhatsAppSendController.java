package com.ivan_degtev.whatsappbotforneoderma.controller;

import com.ivan_degtev.whatsappbotforneoderma.dto.SendingMessageResponse;
import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushService;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.ChatPushSenderServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.ChatpushServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class WhatsAppSendController {

    @Value("${ngrok.url}")
    private String ngrokUrl;
    @Value("${chatpush.api.key}")
    private String chatpushApiKey;

    private final ChatPushService chatPushService;

    public WhatsAppSendController(
            @Value("${ngrok.url}") String ngrokUrl,
            @Value("${chatpush.api.key}")
            String chatpushApiKey,
            @Qualifier("chatPushSenderServiceImpl") ChatPushService chatPushService
    ) {
        this.ngrokUrl = ngrokUrl;
        this.chatpushApiKey = chatpushApiKey;
        this.chatPushService = chatPushService;
    }

    @PostMapping("/send-message")
    public Mono<SendingMessageResponse> sendMessage(
            @RequestParam String text,
            @RequestParam String phone
    ) {
        return chatPushService.sendMessage(text, phone)
                .doOnSuccess(response -> {
                    System.out.println("Message sent successfully: " + response.toString());
                })
                .doOnError(error -> System.err.println("Failed to send message: " + error.getMessage()));
    }
}
