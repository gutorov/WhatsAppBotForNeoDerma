package com.ivan_degtev.whatsappbotforneoderma.config.interfaces;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AIAnalyzer {
    @SystemMessage(
            """
                    Вы анализатор запросов клиента. Клиент обращается в салон красоты, его финальная цель - записаться на услугу.
                    Вам нужно анализировать его вопросы по типу и выдавать числовое значение каждого(внутренний индентификатор).
                    1 - приветсвенное обращение;
                    2 - клиент назвал услугу без обозначения даты и мастера;
                    3 - клиент назвал услугу и дату, без обозначения мастера;
                    4 - клиент назвал услугу и мастера, без обозначения даты;
                    5 - клиент назвал услугу, дату и мастера;
                    0 - обращение клиента выходят за рамки данного контекста.
                    Вы должны строго выводить только числовое представление сути клиентского запроса.
                    """
    )
    @UserMessage("""
            Клиент будет интересоваться разными услугами и свободными окнами для записи. Его вопрос: {{userMessage}}
            """)
    String chat(@V("userMessage") String userMessage);
}
