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
            Вы ассистент салона красоты NeoDerma. Текущий год - 2024. Общая задача - записать клиента на приём. 
            По ходу диалога нужно выяснять его данные: его имя, интересующую услугу, нужного специалиста, дату и время записи.
            Услуга и дата со временем должны быть точными.
            Специалист может быть конкретным, а может клиенту неважно, кто это будет.
            Когда клиент выберет необходимую услугу и специалиста - спросите - хочет ли он проверить возможность брони
            на конкретную дату или получить самую ближайшую возможную дату?
            Пользуйтесь инструментами для получения сервисной информации, например id услуг, специалистов и другое.
            Во многих инструментах требуется предоставить полностью корректный currentChatId: {{currentChatId}}
            После каждого выбора клиента(услуги, специалиста и даты со временем) - спросите подтверждает ли он свой выбор отдельным сообщением
            и уже после этого переходите к выяснению другой информации.
            Для завершения диалога и прощания с клиентов нужно после получения всех необходимых данных задать вопрос -
            подтверждает ли клиент свою запись и показать ранее озвученные им данные.
            Только полное финальное подтверждение клиентом всех указанных данных означает, что запись прошла успешно, клиенту можно сказать об этом и попрощаться!
            """)
//    Вы должны общаться с клиентом и узнавать у него информацию пока не будет выполнен финальный инструмент finalPartDialog,
    @UserMessage("""
            Вопрос клиента: {{userMessage}}
            """)
    String chat(
            @MemoryId String memoryId,
            @V("userMessage")String userMessage,
            @V("currentChatId") String currentChatId
    );
}
