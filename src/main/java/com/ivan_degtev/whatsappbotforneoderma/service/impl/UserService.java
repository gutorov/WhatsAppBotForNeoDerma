package com.ivan_degtev.whatsappbotforneoderma.service.impl;

import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.UserMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.ai.LangChain4jService;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import com.ivan_degtev.whatsappbotforneoderma.service.util.UserChecks;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageService messageService;
    private final LangChain4jService langChain4jService;
    private final JsonLoggingService jsonLogging;

    private final UserChecks userChecks;
    /**
     * Основной метод по мапингу json вопросов-ответов с клиентов во внутренние сущности юзера и сообщения
     * При первичном обращении - юзер создается, если он уже есть в БД по уникальному чат-айди
     * - добавляются только новые сообщения
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void addingUserWhenThereIsNone(WebhookPayload webhookPayload) {
        jsonLogging.info("получил пейлоад из нового метода в сервисе {}", webhookPayload.toString());
        String chatPushMessageId = webhookPayload.getPayload().getNewMessage().getMessage().getId();
        String currentChatId = webhookPayload.getPayload().getNewMessage().getChatId();
        Message currentMessage = new Message();

        if (!userChecks.checkingExistenceUserByChatId(currentChatId)) {
            User user = userMapper.convertWebhookPayloadToUser(webhookPayload);
            currentMessage = messageService.addNewMessage(webhookPayload, chatPushMessageId);

            List<Message> messages = new ArrayList<>();
            messages.add(currentMessage);
            user.setMessages(messages);

            userRepository.save(user);


        } else if (userChecks.checkingExistenceUserByChatId(currentChatId)) {
            currentMessage = messageService.addNextMessage(webhookPayload, chatPushMessageId);


        }

        User currentUser = userRepository.findUserByChatId(currentChatId)
                        .orElseThrow(() -> new NotFoundException("Юзер с айди чата " + currentChatId + " не найден!"));

        currentUser = userChecks.addingUniqueIdForAppointmentIsNone(currentUser);

        langChain4jService.mainMethodByWorkWithLLM(currentUser, currentMessage);
    }

}
