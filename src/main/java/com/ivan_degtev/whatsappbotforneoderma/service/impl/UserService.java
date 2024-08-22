package com.ivan_degtev.whatsappbotforneoderma.service.impl;

import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.UserMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.ai.LangChain4jService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Struct;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageService messageService;
    private final LangChain4jService langChain4jService;
    /**
     * Основной метод по мапингу json вопросов-ответов с клиентов во внутренние сущности юзера и сообщения
     * При первичном обращении - юзер создается, если он уже есть в БД по уникальному чат-айди
     * - добавляются только новые сообщения
     */
    public void addingUserWhenThereIsNone(WebhookPayload webhookPayload) {
        log.info("получил пейлоад из нового метода в сервисе {}", webhookPayload.toString());

        String currentChatId = webhookPayload.getPayload().getNewMessage().getChatId();
        Message currentMessage = new Message();

        if (!CheckingExistenceUserByChatId(currentChatId)) {
            User user = userMapper.convertWebhookPayloadToUser(webhookPayload);
            currentMessage = messageService.addNewMessage(webhookPayload);
            user.setMessages(List.of(currentMessage));
            userRepository.save(user);
        } else if (CheckingExistenceUserByChatId(currentChatId)) {
            currentMessage = messageService.addNextMessage(webhookPayload);
        }

        User currentUser = userRepository.findUserByChatId(currentChatId)
                        .orElseThrow(() -> new NotFoundException("Юзер с айди чата " + currentChatId + " не найден!"));
        /*
          Вытащили уникальное значение, которое приходит из вотсапа для каждого сообщения - будем исопльзоваться,
           как внутренний ключ для связи юзера и текущего Appointment
         */
        String currentUniqueIdForAppointment = webhookPayload.getPayload().getNewMessage().getMessage().getId();
        User currentUser2 = addingUniqueIdForAppointmentIsNone(currentUser, currentUniqueIdForAppointment);

        langChain4jService.mainMethodByWorkWithLLM(currentUser2, currentMessage);
    }

    private User addingUniqueIdForAppointmentIsNone(
            User currentUser,
            String currentUniqueIdForAppointment
    ) {
        if (currentUser.getUniqueIdForAppointment() == null) {
            currentUser.setUniqueIdForAppointment(currentUniqueIdForAppointment);
            userRepository.save(currentUser);
        } else if (currentUser.getAppointments().stream().allMatch(Appointment::getCompletelyFilled)) {
            currentUser.setUniqueIdForAppointment(currentUniqueIdForAppointment);
            userRepository.save(currentUser);
        }
        log.info("Возвращаю из метода addingUniqueIdForAppointmentIsNone текущего юзера с изменениями {}", currentUser);
        return currentUser;
    }

    /**
     * Утилитный метод проверяет есть ли юзер по чат-айди в БД, для понимания были ли с ним ранее диалоги. При отсутсвии -
     * юзер добавляется в БД, при наличии - добавляются только новые сообщения, связываюсь с текущим юзером по чат-айди
     */
    private boolean CheckingExistenceUserByChatId(String currentChatId) {
        return userRepository.existsByChatId(currentChatId);
    }
}
