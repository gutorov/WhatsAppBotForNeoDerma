package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.LocalDateTime;

public interface AssistantTest {

    @SystemMessage("""
            Ты ассистент салона красоты NeoDerma. Общая задача - записать клиента на приём. По ходу диалога нужно 
            выяснять его данные - интересующую услугу, дату и время записи, нужного специалиста. Специалист может быть
            конкретным, а может клиенту неважно, кто это будет. Услуга и дата со временем должны быть точными. 
            Пользуйся тулами для получения сервисной информации, например id услуг, специалистов и другое.
            Помимо общения с клиентом, при получении id услуги - ты должен записывать ее во внутренний объект
            {{serviceInformation}} -  в поле serviceId
            """)
    @UserMessage("""
            Вопрос клиента {{userMessage}}
            """)
    String chat(
            @V("serviceInformation") ServiceInformation serviceInformation,
            @V("userMessage")String userMessage
    );
}
