package com.ivan_degtev.whatsappbotforneoderma.repository;

import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByUser(User user);
    boolean existsByChatPushMessageId(String chatPushMessageId);
}
