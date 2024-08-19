package com.ivan_degtev.whatsappbotforneoderma.config.interfaces;

import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.AvailableStaffForBookingService;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.DataForWriteDTO;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.LocalDateTime;
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
                    Вы полезный ассистент салона красоты НеоДерма. График работы салона 10:00-22:00.
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
                    Всегда отвечайте в человекопонятной форме.
                    """
    )
    @UserMessage("""
            Приветствие клиента: {{message}}
            """)
    String greeting(
            @V("message") String message
            );

    /**
     * 2 - клиент интересуется услугой без обозначения даты и мастера;
     */
    @SystemMessage(
            """
                    Вы полезный ассистент салона красоты NeoDerma.
                    Ваша задача вести с клиентом диалог и привести его к записи на приём к мастеру на нужную услугу.
                    Сейчас клиент указал только интересуется услугой, найдите во внутренней сушности {{serviceInformationDTOList}}
                    есть ли такая услуга по названию или похожие и предложите ему эти варианты.
                    Если клиент просто ведёт речь об услугах - также предложите ему некоторые наши варианты
                    из {{serviceInformationDTOList}}.
                    Спросите какая дата и мастер интересуют клиента. Всегда отвечайте в человекопонятной форме.
                
                    """
    )
    @UserMessage("""
            Клиент общается с вами о нужной ему услуге: {{message}}
            """)
    String onlyServiceDialog(
            @V("serviceInformationDTOList") List<ServiceInformationDTO> serviceInformationDTOList,
            @V("message")String message
    );

    /**
     * 3 - клиент выбрал конкретную услугу и не указал дату
     */
    @SystemMessage(
            """
                    Вы полезный ассистент салона красоты NeoDerma.
                    Ваша задача вести с клиентом диалог и привести его к записи на приём к мастеру на нужную услугу.
                    Сейчас клиент выбрал только нужную услугу и не указал точную дату.
                    В человекочитаемом виде отобразите свободные дни {{availableDatesForBookingServices}} для записи.
                    Спросите, какая дата подходит больше.
                    Всегда отвечайте в человекопонятной форме.

                    """
    )
    @UserMessage("""
            Клиент назвал какую услугу хочет: {{message}}
            """)
    String onlyService(
            @V("availableDatesForBookingServices") String availableDatesForBookingServices,
            @V("message") String message
    );

    /**
     * 4 - клиент выбрал конкретную услугу и указал дату/дату-время
     */
    @SystemMessage(
            """
                    Вы полезный ассистент салона красоты NeoDerma.
                    Ваша задача вести с клиентом диалог и привести его записи на приём к мастеру на нужную услугу.
                    Сейчас клиент выбрал только нужную услугу и указал дату/дату время.
                    В документации {{availableStaffForBookingServiceList}} указаны все сотрудники, делающие эту услугу.
                    Если клиент указал точное время {{message}} скажите, к кому можно записаться в документации
                    {{availableStaffForBookingServiceList}} - поле bookable должно быть true.
                    Если клиент не указал точное время {{message}} - выведите всех сотрудников, что оказывают эту услугу 
                    и спросите кого клиент хочет выбрать или ему непринципиален мастер.
                    Всегда отвечайте в человекопонятной форме.

                    """
    )
    @UserMessage("""
            Клиент назвал дату или дату и время, когда он хочет записаться на услугу {{message}}
            """)
    String onlyServiceAndDate(
            @V("availableStaffForBookingServiceList") List<AvailableStaffForBookingService> availableStaffForBookingServiceList,
            @V("message") String message
            );


    /**
     * Утиличный метод по сохранению данных клиента
     */
    @SystemMessage(
            """
                    Ты внутренний утилитный метод. Твоя задача найти в сообщении человека его имя, если он представлялся.
                    Если имя есть - верните только его, как строку, если имени не было - ничего не делать.
                    Вернуть только имя! Ничего более.
                    """
    )
    @UserMessage("""
            Клиент прислал сообщение: {{message}}
            """)
    String writeUserName(
            @V("dataForWriteDTO") DataForWriteDTO dataForWriteDTO,
            @V("message") String message
    );
    /**
     * Утиличный метод - по упоминаю услуги ищет айди такой услуги из имеющихся данных
     */
    @SystemMessage(
            """
                    Ты внутренний утилитный метод. Твоя задача - найти во внутренних данных {{dataServices}} 
                    сущность, наиболее близко похожую на ту услугу, о которой спрашивал клиент в своем запросе {{message}}
                    Найдя наиболее близкое совпадение по полю title - верни serviceId значение этой сущности.
                    Если клиент обозначил только одну сущность - serviceId тоже должен быть только один!
                    Возвращай только само значение serviceId
                    """
    )
    @UserMessage("""
            Клиент назвал какую услугу хочет: {{message}}
            """)
    String searchFreeDatesByNameService(
            @V("dataServices") List<ServiceInformation> dataServices,
            @V("message")String message
    );

    /**
     * Утилитный метод - возвращает заполненное ДТО записи на сеанс - добавляет туда дату/дату-время
     */
    @SystemMessage("""
            Ты внутренний утилитный метод. Твоя задача - проанализировать ответ клиент, там он должен указать
            желаемую дату или дату/время для бронирвоания услуги.
            Тебе нужно вернуть только дату-время в виде строки.
            Если время не указано точно - должна быть возвращена только дата в виде строки.
            Если не указан год - по умолчанию используй текущий. 
            """)
    @UserMessage("""
            Клиент указал какая дата ему интересна: {{message}}
            """)
    String addingDateForAppointment(
            @V("message") String message
    );

}

