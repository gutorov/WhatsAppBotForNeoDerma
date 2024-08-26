package com.ivan_degtev.whatsappbotforneoderma.service.ai;

import com.ivan_degtev.whatsappbotforneoderma.config.PersistentChatMemoryStore;
import com.ivan_degtev.whatsappbotforneoderma.service.AIMemoryManagementService;
import dev.langchain4j.data.message.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Утилитный сервис для работы(получения и удаления) текущей памяти ЛЛМ по айди памяти(чат айди юзера)
 */
@Service
@AllArgsConstructor
@Slf4j
public class AIMemoryManagementServiceImpl implements AIMemoryManagementService {
    private final PersistentChatMemoryStore persistentChatMemoryStore;

    public List<ChatMessage> getMessages(Object memoryId) {
        List<ChatMessage> resultListChatMessages = persistentChatMemoryStore.getMessages(memoryId);
        log.info("Получил лист со всей текущей сохранённой историей {}, по айди памяти {}",
                resultListChatMessages.toString(), memoryId);
        return resultListChatMessages;
    }
    public void deleteMessages(Object memoryId) {
        persistentChatMemoryStore.deleteMessages(memoryId);
        log.info("Memory cleared for user with memoryId {}", memoryId);
    }
}
