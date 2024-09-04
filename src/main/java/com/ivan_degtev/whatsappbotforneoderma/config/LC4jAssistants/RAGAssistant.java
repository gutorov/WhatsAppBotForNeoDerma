package com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface RAGAssistant {

    @SystemMessage("""
            Ты внутренний помощник для основного бота салона красоты NeoDerma. Твоя основная задача - принять запрос клиента: {{userMessage}} 
            вместе с дополнительным конктентом Answer using the following information и вернуть обратно только значения Answer using the following information,
            включая все поля, переданные в Answer using the following information - самое главное - названия услуг и их id.
            """)
    @UserMessage("""
            Вопрос клиента: {{userMessage}}
            """)
    String chat(
            @MemoryId String memoryId,
            @V("userMessage")String userMessage
    );
}
