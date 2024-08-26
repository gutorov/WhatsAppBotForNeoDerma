package com.ivan_degtev.whatsappbotforneoderma.service;

import dev.langchain4j.data.message.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AIMemoryManagementService {
    List<ChatMessage> getMessages(Object memoryId);
    void deleteMessages(Object memoryId);
}
