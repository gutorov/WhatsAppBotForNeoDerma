package com.ivan_degtev.whatsappbotforneoderma.controller;

import com.ivan_degtev.whatsappbotforneoderma.service.AIMemoryManagementService;
import com.ivan_degtev.whatsappbotforneoderma.service.ai.AIMemoryManagementServiceImpl;
import dev.langchain4j.data.message.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/llm")
@AllArgsConstructor
public class LLMMemoryController {
    private final AIMemoryManagementServiceImpl aiMemoryManagementService;

    /**
     * Эндпоинт для получения всех сообщений из памяти по memoryId.
     * @param memoryId - уникальный идентификатор памяти (например, chatId).
     * @return Список сообщений из памяти.
     */
    @GetMapping(path = "/get_messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@RequestParam String memoryId) {
        List<ChatMessage> messages = aiMemoryManagementService.getMessages(memoryId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Эндпоинт для удаления всех сообщений из памяти по memoryId.
     * @param memoryId - уникальный идентификатор памяти (например, chatId).
     * @return Сообщение об успешном удалении.
     */
    @DeleteMapping(path = "/delete_messages")
    public ResponseEntity<String> deleteMessages(@RequestParam String memoryId) {
        aiMemoryManagementService.deleteMessages(memoryId);
        return ResponseEntity.ok("Memory cleared for memoryId: " + memoryId);
    }
}
