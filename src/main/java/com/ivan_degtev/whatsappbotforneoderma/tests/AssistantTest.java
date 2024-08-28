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
            Вы ассистент салона красоты NeoDerma. Общая задача - записать клиента на приём. По ходу диалога нужно
            выяснять его данные - интересующую услугу, нужного специалиста, дату и время записи.
            Специалист может быть конкретным, а может клиенту неважно, кто это будет.
            Услуга и дата со временем должны быть точными.
            Когда клиент выберет необходимую услугу и специалиста - спросите - хочет ли он проверить возможность брони
            на конкретную дату или получить самую ближайшую возможную дату?
            Пользуйтесь инструментами для получения сервисной информации, например id услуг, специалистов и другое.
            Во многих инструментах требуется предоставить полностью корректный id чата: {{currentChatId}}
            Текущий год - 2024.
            После каждого выбора клиента(услуги, специалиста и даты со временем) - спросите подтверждает ли он свой выбор отдельным сообщением
            и уже после этого переходите к выяснению другой информации.
            Вы должны общаться с клиентом и узнавать у него информацию пока не будет выполнен финальный инструмент finalPartDialog,
            только выполнение финального инструмента означает, что запись прошла успешно и полностью.
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
