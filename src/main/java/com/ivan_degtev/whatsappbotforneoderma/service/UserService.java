package com.ivan_degtev.whatsappbotforneoderma.service;

import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.UserMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageService messageService;

    /**
     * Основной метод по мапингу json вопросов-ответов с клиентов во внутренние сущности юзера и сообщения
     * При первичном обращении - юзер создается, если он уже есть в БД по уникальному чат-айди
     * - добавляются только новые сообщения
     */
    public void addingUserWhenThereIsNone(WebhookPayload webhookPayload) {
        log.info("получил пейлоад из нового метода в сервисе {}", webhookPayload.toString());

        if (!CheckingExistenceUserByChatId(webhookPayload)) {
            User user = userMapper.convertWebhookPayloadToUser(webhookPayload);
            Message message = messageService.addNewMessage(webhookPayload);
            user.setMessages(List.of(message));
            userRepository.save(user);
        } else if (CheckingExistenceUserByChatId(webhookPayload)) {
            messageService.addNextMessage(webhookPayload);
        }
    }

    /**
     * Утилитный метод проверяет есть ли юзер по чат-айди в БД, для понимания были ли с ним ранее диалоги. При отсутсвии -
     * юзер добавляется в БД, при наличии - добавляются только новые сообщения, связываюсь с текущим юзером по чат-айди
     */
    private boolean CheckingExistenceUserByChatId(WebhookPayload webhookPayload) {
        var chatId = webhookPayload.getPayload().getNewMessage().getChatId();
        return userRepository.existsByChatId(chatId);
    }
}
