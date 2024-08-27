package com.ivan_degtev.whatsappbotforneoderma.service.impl;

import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.MessageMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.repository.MessageRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserRepository userRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Message addNewMessage(
            WebhookPayload webhookPayload,
            String chatPushMessageId
    ) {
        Message message = messageMapper.convertWebhookPayloadToMessage(webhookPayload);
        message.setChatPushMessageId(chatPushMessageId);
        log.info("Замапил первое сообщение из вебхука для юзера в БД {}", message.toString());
        messageRepository.save(message);
        log.info("Добавил первое сообщение для юзера в БД {}", message.toString());
        return message;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Message addNextMessage(
            WebhookPayload webhookPayload,
            String chatPushMessageId
    ) {
        var chatId = webhookPayload.getPayload().getNewMessage().getChatId();
        User linkedUser = userRepository.findUserByChatId(chatId)
                .orElseThrow(() -> new NotFoundException("User with this chatId " + chatId + " not found!"));

        Message message = messageMapper.convertWebhookPayloadToMessage(webhookPayload);
        message.setChatPushMessageId(chatPushMessageId);
        log.info("Замапил новое сообщение из вебхука для юзера в БД {}", message.toString());
        message.setUser(linkedUser);
        messageRepository.save(message);
        log.info("Добавил новое сообщение для юзера в БД {}", message.toString());

        return message;
    }
}
