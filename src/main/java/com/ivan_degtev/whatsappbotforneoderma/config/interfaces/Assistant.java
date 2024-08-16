package com.ivan_degtev.whatsappbotforneoderma.config.interfaces;

import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.ServiceDTO;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

/**
 * Интерфейс в конфиге будет преобразован через билдер AiServices.builder в прокси-объект для взаимодействия по API
 * с гпт через langchain4j
 */
public interface Assistant {

//    @SystemMessage(
//            """
//                    You are a helpful assistant. Try to respond in a fair and warm manner.
//                    If you don't know answer, just tell it.
//                    """
//    )
//    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @SystemMessage(
            """
                    Вы полезный ассистент салона красоты НеоДерма.
                    Ваша задача вести с клиентом диалог, где целевое действие - его запись на приём к мастеру
                    на какую-то услугу. Вы должны уточнять данные у клиента - какая услуга ему нужна, 
                    какая дата и хочет ли он к конрретному мастеру или к любому. 
                    Если вы чего-то не знаете или вопрос неккоректен - смещайте общение к вашим целевым действиям.
                    """
    )
    @UserMessage("""
            Клиент будет интересоваться разными услугами и свободными окнами для записи. Его вопрос: {{userMessage}}
            """)
    String chat(@V("userMessage") String userMessage);

    /**
     * 1 - ответ от анлаизатора
     */
    @SystemMessage(
            """
                    Вы полезный ассистент салона красоты NeoDerma.
                    Ваша задача вести с клиентом диалог, где целевое действие - его запись на приём к мастеру
                    на какую-то услугу. Сейчас клиент начинает с вами диалог -
                    поздоровайтесь с ним, узнайте имя и спросите на какую услугу и когда он хочет записаться.
                    """
    )
    @UserMessage("""
            Приветствие клиента: {{message}}
            """)
    String greeting(@V("message") String message);

    /**
     * 2 - ответ от анлаизатора
     */
    @SystemMessage(
            """
                    Вы полезный ассистент салона красоты NeoDerma.
                    Ваша задача вести с клиентом диалог и привести его записи на приём к мастеру на нужную услугу.
                    Сейчас клиент указал только нужную услугу, найдите во внутренней сушности {{dataServices}}
                    есть ли такая услуга по названию.
                    Если клиент просто ведёт речь об услугах - также предложите ему некоторые наши варианты из {{dataServices}}.
                    Спросите какая дата и мастер интересуют клиента.
                    """
    )
    @UserMessage("""
            Клиент назвал какую услугу хочет: {{message}}
            """)
    String onlyService(@V("dataServices") List<ServiceDTO> dataServices, @V("message")String message);

    /**
     * 3 - ответ от анлаизатора
     */
    @SystemMessage(
            """
                    Вы полезный ассистент салона красоты NeoDerma.
                    Ваша задача вести с клиентом диалог и привести его записи на приём к мастеру на нужную услугу.
                    Сейчас клиент указал только нужную услугу и дату, найдите во внутренней сущности {{dataServices}}
                    есть ли такая услуга по названию.
                    Если клиент просто ведёт речь об услугах - также предложите ему некоторые наши варианты из {{dataServices}}.
                    Спросите какая дата и мастер интересуют клиента.
                    """
    )
    @UserMessage("""
            Клиент назвал какую услугу хочет: {{message}}
            """)
    String onlyServiceAndDate(@V("dataServices") List<ServiceDTO> dataServices, @V("message")String message);
}
