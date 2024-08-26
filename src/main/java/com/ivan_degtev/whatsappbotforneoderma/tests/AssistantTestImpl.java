package com.ivan_degtev.whatsappbotforneoderma.tests;

import dev.ai4j.openai4j.chat.Message;
import dev.ai4j.openai4j.chat.ToolCall;
import dev.ai4j.openai4j.chat.UserMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

import dev.langchain4j.model.output.Response;
import dev.langchain4j.data.message.AiMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssistantTestImpl implements AssistantTest {

    private final ChatLanguageModel llm;

    public AssistantTestImpl(ChatLanguageModel llm) {
        this.llm = llm;
    }

    @Override
    public String chat(String memoryId, String userMessage, String currentChatId) {




//
//        List<ChatMessage> messages = new ArrayList<>();
//        UserMessage message = UserMessage.builder()
//                .content(userMessage)
//                .build();
//        messages.add(message);
//
//        // Вызов LLM и получение AIMessage
//        Response<AiMessage> aiMessage = llm.(messages);
//
//        // Логирование tool_calls
//        for (ToolCall toolCall : aiMessage.getToolCalls()) {
//            String toolCallId = toolCall.getId();
//            String functionName = toolCall.getFunction().getName();
//
//            // Вызов соответствующего инструмента
//            String result = invokeToolFunction(functionName, toolCall.getArguments());
//
//            // Отправка ToolMessage
//            processToolResponse(toolCallId, result);
//        }

        // Вернуть результат или продолжить обработку
        return "Обработка завершена";
//        return null;
    }

    private String invokeToolFunction(String functionName, Map<String, Object> arguments) {
        // Логика вызова нужного инструмента по его имени и аргументам
        // Например, вызов метода getFreeDates или getAllServices
        return "Результат работы инструмента";
    }

    private void processToolResponse(String toolCallId, String result) {
        // Логика отправки ответа инструмента обратно в модель
    }
}
