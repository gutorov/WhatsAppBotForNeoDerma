package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.Assistant;
import com.ivan_degtev.whatsappbotforneoderma.config.tools.AssistantToolsUsername;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigTest {

    @Value("${open.ai.token}")
    private String openAiToken;

}
