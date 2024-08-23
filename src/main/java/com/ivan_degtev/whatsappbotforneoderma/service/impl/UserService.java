package com.ivan_degtev.whatsappbotforneoderma.service.impl;

import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.UserMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.ai.LangChain4jService;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageService messageService;
    private final LangChain4jService langChain4jService;
    private final JsonLoggingService jsonLogging;
    private final JsonLoggingService jsonLoggingService;

    /**
     * Основной метод по мапингу json вопросов-ответов с клиентов во внутренние сущности юзера и сообщения
     * При первичном обращении - юзер создается, если он уже есть в БД по уникальному чат-айди
     * - добавляются только новые сообщения
     */
    @Transactional
    public void addingUserWhenThereIsNone(WebhookPayload webhookPayload) {
        jsonLogging.info("получил пейлоад из нового метода в сервисе {}", webhookPayload.toString());

        String currentChatId = webhookPayload.getPayload().getNewMessage().getChatId();
        Message currentMessage = new Message();

        if (!CheckingExistenceUserByChatId(currentChatId)) {
            User user = userMapper.convertWebhookPayloadToUser(webhookPayload);
            currentMessage = messageService.addNewMessage(webhookPayload);

            List<Message> messages = new ArrayList<>();
            messages.add(currentMessage);
            user.setMessages(messages);

            userRepository.save(user);
        } else if (CheckingExistenceUserByChatId(currentChatId)) {
            currentMessage = messageService.addNextMessage(webhookPayload);
        }

        User currentUser = userRepository.findUserByChatId(currentChatId)
                        .orElseThrow(() -> new NotFoundException("Юзер с айди чата " + currentChatId + " не найден!"));

//        String currentUniqueIdForAppointment = webhookPayload.getPayload().getNewMessage().getMessage().getId();
        addingUniqueIdForAppointmentIsNone(currentUser);

        langChain4jService.mainMethodByWorkWithLLM(currentUser, currentMessage);
    }

    private void addingUniqueIdForAppointmentIsNone(User currentUser) {
        if (currentUser.getUniqueIdForAppointment() == null) {
            currentUser.setUniqueIdForAppointment(UUID.randomUUID().toString());
            userRepository.save(currentUser);
        } else if (currentUser.getAppointments().stream().allMatch(Appointment::getCompletedBooking)) {
            currentUser.setUniqueIdForAppointment(UUID.randomUUID().toString());
            userRepository.save(currentUser);
        }
        jsonLogging.info("Возвращаю из метода addingUniqueIdForAppointmentIsNone текущего юзера с изменениями {}",
                currentUser.toString());
    }

    /**
     * Утилитный метод проверяет есть ли юзер по чат-айди в БД, для понимания были ли с ним ранее диалоги. При отсутсвии -
     * юзер добавляется в БД, при наличии - добавляются только новые сообщения, связываюсь с текущим юзером по чат-айди
     */
    private boolean CheckingExistenceUserByChatId(String currentChatId) {
        return userRepository.existsByChatId(currentChatId);
    }
}
