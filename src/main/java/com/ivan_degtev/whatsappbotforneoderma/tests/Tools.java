package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.AvailableSessionDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.FreeSessionForBookDTO;

import com.ivan_degtev.whatsappbotforneoderma.exception.NoParameterException;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;

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
import com.ivan_degtev.whatsappbotforneoderma.service.impl.YClientServiceImpl;

import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import dev.langchain4j.agent.tool.Tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.OffsetDateTime;
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

    private final WebClient webClient = WebClient.builder().build();
    private final ServiceInformationRepository serviceInformationRepository;
    private final AppointmentsRepository appointmentsRepository;
    private final UserRepository userRepository;

    private final JsonLoggingService jsonLogging;
//    private User user;
//    private Appointment appointment;
//    private ServiceInformation serviceInformation;

//    private static final String yclientToken = System.getenv("yclient.token");
//    private static final Long companyId = 316398L;
//    private  static List<ServiceInformationDTO> servicesInformationDTOList;

    public Tools(
            YClientServiceImpl yClientService,
            ServiceMapper serviceMapper,
            EmployeeMapper employeeMapper,
            NearestAvailableSessionMapper nearestAvailableSessionMapper,
            AnswerCheckMapper answerCheckMapper,
            ServiceInformationRepository serviceInformationRepository,
            AppointmentsRepository appointmentsRepository,
            UserRepository userRepository,
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
        this.jsonLogging = jsonLogging;
    }

    @Tool("""
            Получить свободные даты для записи и вывести в человекочитаемом формате. Если дат очень много
            вывести результат в формате "свободны от - и до - "
            """)
    public String getFreeDates(String question) {
        String freeDates = yClientService.getListDatesAvailableForBooking(null).block();
        jsonLogging.info("Тул getFreeDates нашёл свободные даты {}", freeDates);
        return freeDates;
    }

    @Tool("""
            Получить List со всеми услугами, в этом листе есть базовая информация - названия услуг и их внутренний id
            для дальнейшего поиска.
            Исходя из запроса клиента - выведи общую информацию об услуге, которая ему нужна или предложи варианты 
            похожих услуг.
            """)
    //внутренний id услуги
    public List<ServiceInformationDTO> getAllServices(String question) {
        List<ServiceInformationDTO> serviceInformationList = utilGetServicesInformationDTO();
        jsonLogging.info("Тул getAllServices вывел все услуги {}", serviceInformationList);
        return serviceInformationList;
    }

    @Tool("""
            Внутренний инструмент для сохранения выбранных данных.
            Использовать, если клиент  в контексте выбрал на какую услугу он хочет записаться.
            Передай в метод полное название услуги {{serviceName}}, как указано в документации.
            Передай также chatId клиента {{currentChatId}}.
            В ответ ты получишь внутренний id услуги, запомни его.
            """)
    //Если клиент говорит "запишите меня на дизайн стрижку",
    @Transactional
    public String getIdService(
            String serviceName,
            String currentChatId
    ) {
        log.info("Чат айди в getIdService {}", currentChatId);

        List<ServiceInformationDTO> serviceInformationList = utilGetServicesInformationDTO();

        for (ServiceInformationDTO service : serviceInformationList) {
            if (service.getTitle().toLowerCase().contains(serviceName.toLowerCase())) {
                String serviceId = service.getServiceId();

                User currentUser = userRepository.findUserByChatId(currentChatId)
                                .orElseThrow(() -> new NotFoundException("Юзер с чат-id " + currentChatId + " не найден!"));
//                Appointment currentAppointments = new Appointment();
//                currentAppointments.setUniqueIdForAppointment(currentUser.getUniqueIdForAppointment());
                var currentAppointment = getOrCreateActualAppointmentForCurrentSession(currentChatId);
                ServiceInformation currentServiceInformation = new ServiceInformation(serviceId);
                currentAppointment.setServicesInformation(List.of(currentServiceInformation));
                currentUser.setAppointments(List.of(currentAppointment));

//                serviceInformation.setServiceId(serviceId);
//                appointment.setServicesInformation(List.of(serviceInformation));
//                user.setAppointments(List.of(appointment));
                userRepository.save(currentUser);
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
            Если клиент указал название услуги - передай ее id в {{serviceId}}
            Если клиент назвал какое-то имя сотрудника ранее - сравни данные из полученного в результате листа с именем,
            которое он назвал и ответь, есть ли указанный сотрудник в списке бронирования.
            """)
    public List<EmployeeDTO> getListEmployeesForCurrentServices(String serviceId) {

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
            Внутренний инструмент для сохранения выбранных данных.
            Использовать, если пользователь  в контексте выбрал к какому сотруднику он намерен пойти.
            Передай в метод id этого сотрудника в поле {{staffId}}.
            Передай также и chatId клиента {{currentChatId}}.
            """)
    @Transactional
    //Пойми из контекста твёрдость решения пользователя.
    public void confirmationOfEmployeeSelection(
            String staffId,
            String currentChatId
    ) {
        log.info("Чат айди в confirmationOfEmployeeSelection {}", currentChatId);

        if (staffId != null && !staffId.isEmpty()) {
            // Поиск актуального объекта Appointment для текущей сессии
            Appointment actualAppointment = getOrCreateActualAppointmentForCurrentSession(currentChatId);

            actualAppointment.setStaffId(staffId);
            appointmentsRepository.save(actualAppointment);
            jsonLogging.info("Тул confirmationOfEmployeeSelection сохранил в БД обновленную запись с " +
                    "id сотрудника {}", actualAppointment.toString());
        } else {
            jsonLogging.error("Сотрудник с таким id не найден: {}", staffId);
            throw new NotFoundException("Сотрудник с таким id не найден");
        }
    }

    @Tool("""
            Сейчас клиент интересуется самым ближайшим временем, когда можно забронировать. 
            Клиент уже назвал желаемую услугу и выбрал специалиста. Передай в метод выбранные им id услуги {{serviceId}}
            и id сотрудника {{staffId}}
            В ответ ты получишь список с данными по свободным сеансам для брони(дату, время и длину сеанса) 
            - выведи эти данные клиенту.
            """)
    public List<AvailableSessionDTO> getListNearestAvailableSessions(
            String serviceId,
            String staffId
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
            Передай в метод выбранные им дату {{date}}, id услуги {{serviceId}} и id сотрудника {{staffId}}
            В ответ ты получишь список с данными по свободным сеансам для брони(дату, время и длину сеанса)
            - выведи эти данные клиенту.
            """)
    public List<AvailableSessionDTO> getListNearestAvailableSessionsForSpecificDate(
            String date,
            String serviceId,
            String staffId
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
            Передай в метод эту дату и время {{dateTime}} в формате iso8601, данные о доступных датах {{availableSessionDTOS}}
            и chatId клиента {{currentChatId}}
            """)
    //Дата и время, указанные клиентом совпадают с информацией о доступных датах {{availableSessionDTOS}},
    // которую ты получал ранее.
    // Пойми из контекста твёрдость решения пользователя.
    @Transactional
    public void confirmationOfDateSelection(
            String dateTime,
//            List<AvailableSessionDTO> availableSessionDTOS,
            String currentChatId
    ) {
        log.info("Чат айди в confirmationOfDateSelection {}", currentChatId);

        if (dateTime != null) {
            // Поиск актуального объекта Appointment для текущей сессии
            Appointment actualAppointment = getOrCreateActualAppointmentForCurrentSession(currentChatId);

            actualAppointment.setDatetime(OffsetDateTime.parse(dateTime));
            appointmentsRepository.save(actualAppointment);
            jsonLogging.info("Тул confirmationOfDateSelection сохранил в БД обновленную запись " +
                            "с актуальной датой и временем записи {}", actualAppointment.toString());
        } else {
            jsonLogging.error("Указанная клиентом дата либо пустая, либо отсутствует в списке доступных " +
                            "дат для бронирования: {}", dateTime);
            throw new NotFoundException("Указанная клиентом дата либо пустая, " +
                    "либо отсутствует в списке доступных дат для бронирования");
        }
    }
    @Tool("""
            Последний инструмент в цепочки взаимодействий с клиентом. Клиент выбрал и подтвердил услугу, сотрудника и дату
            со временем, на которые он записывается. Эти данные были обозначены и ты использовал все внутренние инструменты
            для сохранения данных.
            Здесь происходит финальная проверка - если все успешно - с клиентом можно попрощаться и сообщить об успехе
            записи, выведя данные о его сеансе.
            Если есть проблемы - значит какая-то информация не была сохранена и ее нужно уточнить - выполнил соответствующие
            инструменты.
            Передай сюда chatId клиента {{currentChatId}}
            """)
    @Transactional
    public boolean finalPartDialog(String currentChatId) {
        log.info("Чат айди в finalPartDialog {}", currentChatId);

        // Поиск актуального объекта Appointment для текущей сессии
        Appointment actualAppointment = getOrCreateActualAppointmentForCurrentSession(currentChatId);

        //ВАЖНО! Текущий код должен работать только при записи на 1 услугу.
        // ВАЖНО! После рефа, когда будем создавать записи на неск. услугу - переделать проверки
        ServiceInformation currentServiceInformation = serviceInformationRepository
                .findAllByAppointment(actualAppointment).get(0);
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
    public Appointment getOrCreateActualAppointmentForCurrentSession(String currentChatId) {
        User currentUser = userRepository.findUserByChatId(currentChatId)
                .orElseThrow(() -> new NotFoundException("User с id чата " + currentChatId + " не найден"));
        String currentUniqueIdForAppointment = currentUser.getUniqueIdForAppointment();

        // Ищем Appointment, если не находим - создаем новый
        Appointment actualAppointment = appointmentsRepository
                .findByUser_UniqueIdForAppointment(currentUniqueIdForAppointment)
                .orElseGet(() -> {
//                    currentUser = userRepository.save(currentUser);

                    Appointment newAppointment = new Appointment();
                    newAppointment.setUniqueIdForAppointment(currentUniqueIdForAppointment);
//                    newAppointment.setUser(currentUser);
                    currentUser.setAppointments(List.of(newAppointment));
                    return appointmentsRepository.save(newAppointment);
                });

        return actualAppointment;
    }

}
