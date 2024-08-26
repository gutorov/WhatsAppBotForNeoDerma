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
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserRepository userRepository;

    @Transactional
    public Message addNewMessage(WebhookPayload webhookPayload) {
        Message message = messageMapper.convertWebhookPayloadToMessage(webhookPayload);
        messageRepository.save(message);
        return message;
    }

    @Transactional
    public Message addNextMessage(WebhookPayload webhookPayload) {
        var chatId = webhookPayload.getPayload().getNewMessage().getChatId();
        User linkedUser = userRepository.findUserByChatId(chatId)
                .orElseThrow(() -> new NotFoundException("User with this chatId " + chatId + " not found!"));

        Message message = messageMapper.convertWebhookPayloadToMessage(webhookPayload);
        message.setUser(linkedUser);
        messageRepository.save(message);

        return message;
    }
}
