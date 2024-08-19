package com.ivan_degtev.whatsappbotforneoderma.config;

import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.AIAnalyzer;
import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.Assistant;
import com.ivan_degtev.whatsappbotforneoderma.config.tools.AssistantToolsUsername;
import com.ivan_degtev.whatsappbotforneoderma.controller.YClientController;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.tests.AssistantTest;
import com.ivan_degtev.whatsappbotforneoderma.tests.Tools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Value("${open.ai.token}")
    private String openAiToken;
    private final ServiceMapper serviceMapper;

    public AIConfig(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }

    @Bean
    public AIAnalyzer aiServices() {
        return AiServices.builder(AIAnalyzer.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
    /**
     * Настрйока ассистента - интерфейса, через AiServices билдер клиента
     * для работы с яз. моделями через фреймворк langchain4j
     */
    @Bean
    public Assistant assistant() {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(1))
                .tools()
                .build();
    }

    /**
     * Настройка самой языковой модели
     * Для демонстрации - использовать апи-ключ "демо"(работае из РФ), по идее, должно работать с реальным ключом -
     * для этого исп. закоментированное значение и добавить ключ в пропертя или в переменные среды
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiToken)
//                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
//                .responseFormat("json_object")
                .logRequests(true)
                .logRequests(true)
                .build();
    }

    @Bean
    public AssistantTest assistantTest() {
        return AiServices.builder(AssistantTest.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .tools(new Tools(serviceMapper))
                .build();
    }
}
