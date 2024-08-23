package com.ivan_degtev.whatsappbotforneoderma.service;

import com.ivan_degtev.whatsappbotforneoderma.dto.SendingMessageResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public abstract class ChatPushServiceAdapter implements ChatPushService {


    @Override
    public Mono<SendingMessageResponse> sendMessage(String text, String phone) {
        return Mono.empty();
    }

    @Override
    public void getMessageFromWebhook(
            Map<String, String> headers,
            String payload
    ) {}

    @Override
    public Mono<String> createWebhook(String url, List<String> types) {
        return Mono.empty();
    }

    @Override
    public Mono<Map<String, Object>> getAllWebhooks() {
        return Mono.empty();
    }

    @Override
    public Mono<String> deleteWebhook(int webhookId) {
        return Mono.empty();
    }
}
