package com.ivan_degtev.whatsappbotforneoderma.service;

import com.ivan_degtev.whatsappbotforneoderma.dto.SendingMessageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public interface ChatPushService {
    /**
     * Общий метод для всех классов по работе с ЧатПуш, создаёт и настраивает веб-хук при первичном запуске программы.
     * Вызывается 1 раз при первом запуске.
     * Это нужно для коннекта через сервис чатпуш + пррокси приложение(ngrok в нашем случае) + наше веб-приложение
     */
    Mono<String> createWebhook(String url, List<String> types);

    /**
     * Получение всех(и входящих и исходящих) сообщений через активированные веб-хуки
     */
    void getMessageFromWebhook(
            Map<String, String> headers,
            String payload
    );

    /**
     * Отправки сообщений через сервис ЧатПуш по заданному url
     * @param text
     * @param phone
     * @return Замапленную в локал дто сущность ответа об отправке с данными
     */
    Mono<SendingMessageResponse> sendMessage(String text, String phone);
}
