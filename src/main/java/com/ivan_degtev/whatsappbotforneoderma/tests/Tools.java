package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.RAGAssistant;
import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.AvailableSessionDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.FreeSessionForBookDTO;

import com.ivan_degtev.whatsappbotforneoderma.exception.NoParameterException;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;

import com.ivan_degtev.whatsappbotforneoderma.exception.NotSaveDataException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.AnswerCheckMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.NearestAvailableSessionMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;

import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.AppointmentsRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient.YClientServiceImpl;

import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Tools {

    private final YClientServiceImpl yClientService;
    private final ServiceMapper serviceMapper;
    private final EmployeeMapper employeeMapper;
    private final NearestAvailableSessionMapper nearestAvailableSessionMapper;
    private final AnswerCheckMapper answerCheckMapper;

    private final ServiceInformationRepository serviceInformationRepository;
    private final AppointmentsRepository appointmentsRepository;
    private final UserRepository userRepository;

    private final RAGAssistant ragAssistant;

    private final JsonLoggingService jsonLogging;

    private final static String messageAboutSuccessfulSaving = "Данные клиента успешно сохранены во внутреннюю базу данных!";

    public Tools(
            YClientServiceImpl yClientService,
            ServiceMapper serviceMapper,
            EmployeeMapper employeeMapper,
            NearestAvailableSessionMapper nearestAvailableSessionMapper,
            AnswerCheckMapper answerCheckMapper,
            ServiceInformationRepository serviceInformationRepository,
            AppointmentsRepository appointmentsRepository,
            UserRepository userRepository,
            RAGAssistant ragAssistant,
            JsonLoggingService jsonLogging
    ) {
        log.info("Создаем Tools с инжектированными зависимостями");

        this.yClientService = yClientService;
        this.serviceMapper = serviceMapper;
        this.employeeMapper = employeeMapper;
        this.nearestAvailableSessionMapper = nearestAvailableSessionMapper;
        this.answerCheckMapper = answerCheckMapper;
        this.serviceInformationRepository = serviceInformationRepository;
        this.appointmentsRepository = appointmentsRepository;
        this.userRepository = userRepository;
        this.ragAssistant = ragAssistant;
        this.jsonLogging = jsonLogging;
    }

    @Tool("""
            Клиент назвал или упомянул своё имя в контексте. Тебе нужно выделить это имя из контекста разговора
            и передать в метод - имя клиента: {{userName}} и его currentChatId: {{currentChatId}}
            """)
    @Transactional
    public String saveUserName(
            @P("Имя клиента")String userName,
            @P("ID текущего чата") String currentChatId
    ) {
        if (userName == null || currentChatId == null) {
            throw new NoParameterException("В тул не переданы имя клиента или id чата для сохранения");
        }
        try {
            User currentUser = userRepository.findUserByChatId(currentChatId)
                    .orElseThrow(() -> new NotFoundException("Юзер с айди чата " + currentChatId + " не найден!"));
            currentUser.setSenderName(userName);

            userRepository.save(currentUser);
            return messageAboutSuccessfulSaving;
        } catch (NotSaveDataException ex) {
            log.error("Ошибка при сохранении имени клиента для чата {}: {}", currentChatId, ex.getMessage());
            return "Ошибка при сохранении имени. Пожалуйста, попробуйте еще раз позже.";
        }
    }

    @Tool("""
            Клиент назвал или упомянул адрес своей електронной почты. Тебе нужно выделить этот email из контекста разговора и передать в метод -  
            адрес электронной почты клиента: {{email}} и его currentChatId: {{currentChatId}}
            """)
    @Transactional
    public String saveEmail(
            @P("Электронный адрес клиента") String email,
            @P("ID текущего чата") String currentChatId
    ) {
        if (email == null || currentChatId == null) {
            throw new NoParameterException("В тул не переданы email клиента или id чата для сохранения");
        }
        try {
            User currentUser = userRepository.findUserByChatId(currentChatId)
                    .orElseThrow(() -> new NotFoundException("Юзер с айди чата " + currentChatId + " не найден!"));
            currentUser.setEmail(email);
            userRepository.save(currentUser);

            return messageAboutSuccessfulSaving;
        } catch (NotSaveDataException ex) {
            log.error("Ошибка при сохранении электронной почты клиента для чата {}: {}", currentChatId, ex.getMessage());
            return "Ошибка при сохранении электронной почты. Пожалуйста, попробуйте еще раз позже.";
        }
    }

    @Tool("""
            Передай сюда запрос клиента {{question}} и chat Id {{currentChatId}}.
            Получить строку со всеми услугами, в этой строке есть базовая информация - названия услуг, цена и их внутренний id
            для дальнейшего поиска.
            Исходя из запроса клиента - выведи общую информацию об услуге - название и цену или предложи клиенту все варианты услуг с ценами.
            ID услуги не выводи, но запомни, это потребуется в дальнейшем.
            """)
    public String getAllServices(
            @P("Вопрос клиента с упоминанием нужной ему услуги") String question,
            @P("Текущий id чата") String currentChatId
    ) {
        try {
            String response = ragAssistant.chat(currentChatId, question);
            return response;
        } catch (Exception e) {
            jsonLogging.error("Ошибка при вызове ragAssistant: {}", e.getMessage());
            return "{\"error\":\"Ошибка при вызове ragAssistant\", \"details\":\"" + e.getMessage() + "\"}";
        }
    }

    @Tool("""
            Внутренний инструмент для сохранения данных о выбранной услуге.
            Использовать, если клиент  в контексте выбрал на какую услугу он хочет записаться.
            Передай в метод полное название услуги: {{serviceName}}, как указано в документации и chatId клиента: {{currentChatId}}.
            В ответ ты получишь внутренний id услуги, запомни его.
            """)
    @Transactional
    public String getIdService(
            @P("Название услуги, о которой говорил клиент") String serviceName,
            @P("ID текущего чата")String currentChatId
    ) {
        log.info("Чат айди в getIdService {}", currentChatId);

        List<ServiceInformationDTO> serviceInformationList = utilGetServicesInformationDTO();

        for (ServiceInformationDTO service : serviceInformationList) {
            if (service.getTitle().toLowerCase().contains(serviceName.toLowerCase())) {
                String serviceId = service.getServiceId();

                User currentUser = userRepository.findUserByChatId(currentChatId)
                                .orElseThrow(() -> new NotFoundException("Юзер с чат-id " + currentChatId + " не найден!"));
                var currentAppointment = getOrCreateActualAppointmentForCurrentSession(
                        currentUser,
                        currentChatId
                );
                String priceMin = service.getPriceMin();
                String priceMax = service.getPriceMax();
                ArrayList<ServiceInformation> serviceInfoList = new ArrayList<>();
                ArrayList<Appointment> appointmentList = new ArrayList<>();

                ServiceInformation currentServiceInformation = new ServiceInformation(serviceId, serviceName, priceMin, priceMax);
                serviceInfoList.add(currentServiceInformation);

                currentAppointment.setServicesInformation(serviceInfoList);
                appointmentList.add(currentAppointment);

                currentUser.setAppointments(appointmentList);

                userRepository.save(currentUser);
//                entityManager.flush();
                jsonLogging.info("Тул getIdService нашёл совпадение по имени услуги и записал в сущность " +
                                "и сохранил в юзера {}", currentServiceInformation.toString());
                return serviceId;
            }
        }
        jsonLogging.error("Услуга не найдена: {}", serviceName);
        throw new NotFoundException("Услуга с таким названием не найдена");
    }

    @Tool("""
            Дать информацию клиенту обо всех доступных для бронирования сотрудниках.
            Если клиент указал название услуги - передай id этой услуги в: {{serviceId}}
            Если клиент назвал какое-то имя сотрудника ранее - сравни данные из полученного в результате листа с именем,
            которое он назвал и ответь, есть ли указанный сотрудник в списке бронирования.
            """)
    public List<EmployeeDTO> getListEmployeesForCurrentServices(
            @P("ID услуги, о которой говорил клиент") String serviceId
    ) {

        String staffs;
        if (serviceId != null || !serviceId.isEmpty()) {
             staffs = yClientService.getListEmployeesAvailableForBooking
                            (List.of(serviceId), null)
                    .block();
        } else {
            staffs = yClientService.getListEmployeesAvailableForBooking
                            (null, null)
                    .block();
        }

        List<EmployeeDTO> employeeDTOList = employeeMapper.mapJsonToEmployeeList(staffs);

        jsonLogging.info("Тул getListEmployeesForCurrentServices нашёлл конвертированный лист" +
                        " с ДТО с данными о работниках: {}", employeeDTOList);
        return employeeDTOList;
    }
    @Tool("""
            Внутренний инструмент для сохранения данных о выбранном сотруднике.
            Использовать, если пользователь  в контексте выбрал к какому сотруднику он намерен пойти.
            Передай сюда id выбранного сотрудника: {{staffId}} и id чата клиента: {{currentChatId}}.
            """)
    @Transactional
    public String confirmationOfEmployeeSelection(
            @P("ID сотрудника, которого выбрал клиент") String staffId,
            @P("ID текущего чата")String currentChatId
    ) {
        log.info("Чат айди в confirmationOfEmployeeSelection {}", currentChatId);

        if (staffId == null || staffId.isEmpty()) {
            throw new NoParameterException("В тул не переданы id выбранного клиентом сотрудника для сохранения");
        }
        try {
            User currentUser = userRepository.findUserByChatId(currentChatId)
                    .orElseThrow(() -> new NotFoundException("User с id чата " + currentChatId + " не найден"));
            Appointment actualAppointment = getOrCreateActualAppointmentForCurrentSession(
                    currentUser,
                    currentChatId
            );

            actualAppointment.setStaffId(staffId);
            appointmentsRepository.save(actualAppointment);
            jsonLogging.info("Тул confirmationOfEmployeeSelection сохранил в БД обновленную запись с " +
                    "id сотрудника {}", actualAppointment.toString());

            return messageAboutSuccessfulSaving;
        }  catch (NotSaveDataException ex) {
            log.error("Ошибка при сохранении выбранного сотрудника по его id {}: {}", staffId, ex.getMessage());
            return "Ошибка при сохранении выбранного сотрудника. Пожалуйста, попробуйте еще раз позже.";
        }
    }

    @Tool("""
            Сейчас клиент интересуется самым ближайшим временем, когда можно забронировать. 
            Клиент уже назвал желаемую услугу и выбрал специалиста. Передай в метод выбранные им id услуги: {{serviceId}}
            и id сотрудника: {{staffId}}
            В ответ ты получишь список с данными по свободным сеансам для брони(дату, время и длину сеанса) 
            - выведи эти данные клиенту.
            """)
    public List<AvailableSessionDTO> getListNearestAvailableSessions(
            @P("ID услуги, которую выбрал клиент") String serviceId,
            @P("ID сотрудника, которого выбрал клиент") String staffId
    ) {
        if (staffId == null || staffId.isEmpty()) {
            throw new NoParameterException("Не был передам id услуги для которой нужно произвести поиск свободных для " +
                    "бронирования сенсов!");
        }
        List<String> listWithServiceIds = List.of(serviceId);
        String response = yClientService.getListNearestAvailableSessions(
                Long.valueOf(staffId),
                listWithServiceIds)
                .block();
        List<AvailableSessionDTO> availableSessionDTOS =
                nearestAvailableSessionMapper.mapJsonToNearestAvailableSessionList(response);
        jsonLogging.info("Тул getListNearestAvailableSessions получил из мапера лист с доступными сеансами " +
                        "для брони {}",
                availableSessionDTOS.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
        return availableSessionDTOS;
    }

    @Tool("""
            Сейчас клиент интересуется можно ли записаться на сеанс строго на определённую дату.
            Клиент уже назвал нужную ему дату, желаемую услугу и выбрал специалиста.
            Передай в метод выбранные им дату: {{date}}, id услуги: {{serviceId}} и id сотрудника: {{staffId}}
            В ответ ты получишь список с данными по свободным сеансам для брони(дату, время и длину сеанса)
            - выведи эти данные клиенту.
            """)
    public List<AvailableSessionDTO> getListNearestAvailableSessionsForSpecificDate(
            @P("Дата, на которую хочет записаться клиент") String date,
            @P("ID услуги, которую выбрал клиент") String serviceId,
            @P("ID сотрудника, которого выбрал клиент") String staffId
    ) {
        String response = yClientService.getListSessionsAvailableForBooking(
                date,
                Long.valueOf(staffId),
                List.of(serviceId))
                .block();
        List<AvailableSessionDTO> availableSessionDTOS =
                nearestAvailableSessionMapper.mapJsonToAvailableSessionListForSpecificDate(response);
        jsonLogging.info("Тул getListNearestAvailableSessionsForSpecificDate получил из мапера лист " +
                        "с доступными сеансами для брони строго на заданную дату {}",
                availableSessionDTOS.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
        return availableSessionDTOS;
    }

    @Tool("""
            Если клиент точно выбрал и подтвердил на какую дату и время ему необходима запись.
            Ранее он выбрал сотрудника и услугу и ты использовал внутренние инструменты для сохранения этих данных!
            Передай в метод эту дату и время: {{dateTime}} в формате iso8601 и chatId клиента: {{currentChatId}}
            """)
    @Transactional
    public String confirmationOfDateSelection(
            @P("Дата и время в полном формате, на которые записывается клиент") String dateTime,
            @P("ID текущего чата") String currentChatId
    ) {
        if (dateTime == null) {
            throw new NoParameterException("Не была передана дата для сохранения!");
        }
        try {
            User currentUser = userRepository.findUserByChatId(currentChatId)
                    .orElseThrow(() -> new NotFoundException("User с id чата " + currentChatId + " не найден"));

            Appointment actualAppointment = getOrCreateActualAppointmentForCurrentSession(
                    currentUser,
                    currentChatId
            );

            OffsetDateTime parsedDateTime = OffsetDateTime.parse(dateTime);
            ZonedDateTime zonedDateTime = parsedDateTime.atZoneSameInstant(ZoneId.of("Asia/Krasnoyarsk"));
            OffsetDateTime dateTimeWithOffset = zonedDateTime.toOffsetDateTime();

            actualAppointment.setDatetime(dateTimeWithOffset);
            appointmentsRepository.save(actualAppointment);
            jsonLogging.info("Тул confirmationOfDateSelection сохранил в БД обновленную запись " +
                    "с актуальной датой и временем записи {}", actualAppointment.toString());

            return messageAboutSuccessfulSaving;
        } catch (NotSaveDataException ex) {
            jsonLogging.error("Указанная клиентом дата-время либо пустая, либо отсутствует в списке доступных " +
                    "дат для бронирования: {}", dateTime);
            return "Ошибка при сохранении выбранной даты-времени. Пожалуйста, попробуйте еще раз позже.";
        }
    }

    @Tool("""
            Последний инструмент в цепочки взаимодействий с клиентом. Клиент выбрал и подтвердил услугу, сотрудника и дату
            со временем, на которые он записывается. Эти данные были обозначены и подтверждены.
            Здесь происходит финальная проверка - если все успешно - с клиентом можно попрощаться и сообщить об успехе
            записи, выведя данные о его сеансе.
            Если есть проблемы - значит какая-то информация не была сохранена и ее нужно уточнить - выполнил соответствующие
            инструменты.
            Передай сюда chatId клиента: {{currentChatId}}
            """)
    @Transactional
    public boolean finalPartDialog(
            @P("ID текущего чата") String currentChatId
    ) {
        log.info("Чат айди в finalPartDialog {}", currentChatId);

        // Поиск актуального объекта Appointment для текущей сессии
        User currentUser = userRepository.findUserByChatId(currentChatId)
                .orElseThrow(() -> new NotFoundException("User с id чата " + currentChatId + " не найден"));
        Appointment actualAppointment = getOrCreateActualAppointmentForCurrentSession(
                currentUser,
                currentChatId
        );

        //ВАЖНО! Текущий код должен работать только при записи на 1 услугу.
        // ВАЖНО! После рефа, когда будем создавать записи на неск. услугу - переделать проверки
        ServiceInformation currentServiceInformation = serviceInformationRepository
                .findAllByAppointment(actualAppointment).get(0);
        if(currentUser.getSenderName() == null) {
            throw new NoParameterException("В сущности user не сохранено имя клиента");
        }
        if (currentUser.getEmail() == null) {
            throw new NoParameterException("В сущности user не сохранена електронная почта клиента");
        }
        if (actualAppointment.getDatetime() == null) {
            throw new NoParameterException("В сущности appointment не сохранена дата и время записи на приём");
        }
        if (actualAppointment.getStaffId() == null) {
            throw new NoParameterException("В сущности appointment не сохранен id сотрудника к которому нужно " +
                    "записаться на приём");
        }
        if (actualAppointment.getServicesInformation() == null) {
            throw new NoParameterException("В сущности appointment нет связи с сущностью ServiceInformation," +
                    "которая хранит данные о забронированных услугах");
        }
        if (currentServiceInformation.getServiceId() == null) {
            throw new NoParameterException("В сущности serviceInformation не сохранен id услуги для записи на приём");
        }
        String finalCheck = yClientService.getListSessionsAvailableForBooking(
                String.valueOf(actualAppointment.getDatetime()),
                Long.valueOf(actualAppointment.getStaffId()),
                List.of(currentServiceInformation.getServiceId())
        ).block();
        FreeSessionForBookDTO freeSessionForBookDTO = answerCheckMapper.mapJsonToFreeSessionForBookDTO(finalCheck);

        if (freeSessionForBookDTO != null &&
                freeSessionForBookDTO.isSuccess() &&
                freeSessionForBookDTO.getData() != null &&
                freeSessionForBookDTO.getData()
                        .stream()
                        .anyMatch(dataInfo -> dataInfo.getDateTime()
                                .equals(actualAppointment.getDatetime()))) {
            actualAppointment.setCompletedBooking(true);
            appointmentsRepository.saveAndFlush(actualAppointment);
            // САМЫЙ ВАЖНЫЙ ФЛАГ - СЧИТЫВАЕТСЯ В ОСНОВНОМ КОДЕ ПРИЛОЖЕНИЯ
            // И ПО НЕМУ ПОНИМАЕМ, ЧТО ЗАПИСЬ СОБРАНА И МОЖНО ОТПРАВЛЯТЬ POST-ЗАПРОС НА YACLIENT
            jsonLogging.info("Тул finalPartDialog подтвердил заполненность всех данных и поставил флаг завершённости" +
                    "в объект actualAppointment, сохранил в базу {}", actualAppointment.toString());
            return true;
        } else {
            return false;
        }
    }


    /**
     * Отдаёт лист дто со всей базовой инфой по услугам - айди и название, без параметров, вообще все
     */
    private List<ServiceInformationDTO> utilGetServicesInformationDTO() {
        String response = yClientService.getListServicesAvailableForBooking(
                null, null, null
        )
                .block();
        return serviceMapper.mapJsonToServiceList(response);
    }

    @Transactional
    public Appointment getOrCreateActualAppointmentForCurrentSession(
            User currentUser,
            String currentChatId
    ) {
        String currentUniqueIdForAppointment = currentUser.getUniqueIdForAppointment();

        Appointment actualAppointment = appointmentsRepository
                .findByUser_UniqueIdForAppointment(currentUniqueIdForAppointment)
                .orElseGet(() -> {

                    ArrayList<Appointment> appointmentList = new ArrayList<>();
                    Appointment newAppointment = new Appointment();
                    newAppointment.setUniqueIdForAppointment(currentUniqueIdForAppointment);
                    appointmentList.add(newAppointment);

                    currentUser.setAppointments(appointmentList);

                    return appointmentsRepository.save(newAppointment);
                });

        return actualAppointment;
    }

}
