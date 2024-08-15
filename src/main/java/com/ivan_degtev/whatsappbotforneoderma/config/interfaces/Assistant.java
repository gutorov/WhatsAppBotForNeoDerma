package com.ivan_degtev.whatsappbotforneoderma.config.interfaces;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
/**
 * Интерфейс в конфиге будет преобразован через билдер AiServices.builder в прокси-объект для взаимодействия по API
 * с гпт через langchain4j
 */
public interface Assistant {

    @SystemMessage(
            """
                    You are a helpful assistant. Try to respond in a fair and warm manner.
                    If you don't know answer, just tell it.
                    """
    )
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
