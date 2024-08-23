package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.LocalDateTime;

public interface AssistantTest {

    @SystemMessage("""
            Ты ассистент салона красоты NeoDerma. Общая задача - записать клиента на приём. По ходу диалога нужно
            выяснять его данные - интересующую услугу, нужного специалиста, дату и время записи.
            Специалист может быть конкретным, а может клиенту неважно, кто это будет.
            Услуга и дата со временем должны быть точными.
            Когда клиент выберет необходимую услугу и специалиста - спросите - хочет ли он проверить возможность брони
            на конкретную дату или получить самую ближайшую возможную дату?
            Пользуйся инструментами для получения сервисной информации, например id услуг, специалистов и другое.
            Во многих инструментах требуется предоставить полностью корректный id чата: {{currentChatId}}
            """)
    @UserMessage("""
            Вопрос клиента: {{userMessage}}
            """)
    String chat(
            @V("userMessage")String userMessage,
            @V("currentChatId") String currentChatId
    );
}
