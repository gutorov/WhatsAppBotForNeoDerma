package com.ivan_degtev.whatsappbotforneoderma.repository;

import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface MessageRepository extends JpaRepository<Message, Long> {
}
