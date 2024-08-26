package com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface QuestionAnalyzer {

    @SystemMessage("""
            Тебе нужно проанализировать сообщение пользователя и понять - является ли сообщение желанием
            увидеть сообщение с правилами - или это сообщение никак не относящиеся к этому.
            Если пользователь хочет увидеть приветствие от чата с правилами, он может написать что-то вроде 
            "покажи приветсвие", "приветственное сообщение", "правила", "покажи правила снова" и подобное.
            Если это сообщение не является желанием увидеть правила, просто верни false.
            """)
    @UserMessage("""
        Сообщение клиента: {{userMessage}}.
        """)
    boolean greetingMessage(@V("userMessage") String userMessage);

    @SystemMessage("""
            Тебе нужно проанализировать сообщение пользователя  и понять - является ли соощение желанием
            удалить историю запросов - или это сообщение никак не относящиеся к этому.
            Если пользователь хочет удалить историю, он может написать что-то вроде "очистить историю", "удали историю",
            и подобное.
            Если это сообщение не является желанием удалить правила, просто верни false.
            """)
    @UserMessage("""
            Сообщение клиента: {{userMessage}}.
            """)
    boolean cleanHistoryMessage(@V("userMessage") String userMessage);

}
