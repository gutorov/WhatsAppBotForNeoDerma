package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.LocalDateTime;

public interface AssistantTest {

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

    @SystemMessage("""
            Ты ассистент салона красоты NeoDerma. Общая задача - записать клиента на приём. По ходу диалога нужно
            выяснять его данные - интересующую услугу, нужного специалиста, дату и время записи.
            Специалист может быть конкретным, а может клиенту неважно, кто это будет.
            Услуга и дата со временем должны быть точными.
            Когда клиент выберет необходимую услугу и специалиста - спросите - хочет ли он проверить возможность брони
            на конкретную дату или получить самую ближайшую возможную дату?
            Пользуйся инструментами для получения сервисной информации, например id услуг, специалистов и другое.
            Во многих инструментах требуется предоставить полностью корректный id чата: {{currentChatId}}
            Текущий год - 2024.
            """)
    @UserMessage("""
            Вопрос клиента: {{userMessage}}
            """)
    String chat(
            @MemoryId String memoryId,
            @V("userMessage")String userMessage,
            @V("currentChatId") String currentChatId
    );
}
