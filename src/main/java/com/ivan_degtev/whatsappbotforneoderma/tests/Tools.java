package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.AvailableSessionDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.exception.NoParameterException;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.AvailableSessionMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.AppointmentsRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.YClientServiceImpl;
import dev.langchain4j.agent.tool.Tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Tools {
    private final YClientServiceImpl yClientService;
    private final ServiceMapper serviceMapper;
    private final EmployeeMapper employeeMapper;
    private final AvailableSessionMapper availableSessionMapper;

    private final WebClient webClient = WebClient.builder().build();
    private final ServiceInformationRepository serviceInformationRepository;
    private final AppointmentsRepository appointmentsRepository;
    private final UserRepository userRepository;
    private User user;
    private Appointment appointment;
    private ServiceInformation serviceInformation;

    private static final String yclientToken = System.getenv("yclient.token");
    private static final Long companyId = 316398L;
    private  static List<ServiceInformationDTO> servicesInformationDTOList;

    public Tools(
            YClientServiceImpl yClientService,
            ServiceMapper serviceMapper,
            EmployeeMapper employeeMapper,
            AvailableSessionMapper availableSessionMapper,
            ServiceInformationRepository serviceInformationRepository,
            AppointmentsRepository appointmentsRepository,
            UserRepository userRepository,
            User user,
            Appointment appointment,
            ServiceInformation serviceInformation
    ) {
        this.yClientService = yClientService;
        this.serviceMapper = serviceMapper;
        this.employeeMapper = employeeMapper;
        this.availableSessionMapper = availableSessionMapper;
        this.serviceInformationRepository = serviceInformationRepository;
        this.appointmentsRepository = appointmentsRepository;
        this.userRepository = userRepository;
        this.user = user;
        this.appointment = appointment;
        this.serviceInformation = serviceInformation;
    }

    @Tool("""
            Получить свободные даты для записи и вывести в человекочитаемом формате. Если дат очень много
            вывести результат в формате "свободны от - и до - "
            """)
    public String getFreeDates(String question) {
        String freeDates = yClientService.getListDatesAvailableForBooking(null).block();
        log.info("Тул нашёл свободные даты {}", freeDates);
        return freeDates;
    }
    @Tool("""
            Получить List со всеми услугами, в этом листе есть базовая информация - названия услуг и их внутренний id
            для дальнейшего поиска. Исходя из запроса клиента - выведи внутренний id услуги, которая ему нужна.
            """)
    public List<ServiceInformationDTO> getAllServices(String question) {
        List<ServiceInformationDTO> serviceInformationList = utilGetServicesInformationDTO();
                log.info("Тул вывел все услуги {}", serviceInformationList);
        return serviceInformationList;
    }

    @Tool("""
    По запросу пользователя найди услугу и запиши её serviceId в объект.
    Например, если пользователь говорит "запишите меня на дизайн стрижку", найди и верни ID услуги "дизайнерская стрижка".
    """)
    @Transactional
    public String getIdService(String serviceName) {
        List<ServiceInformationDTO> serviceInformationList = utilGetServicesInformationDTO();

        for (ServiceInformationDTO service : serviceInformationList) {
            if (service.getTitle().toLowerCase().contains(serviceName.toLowerCase())) {
                String serviceId = service.getServiceId();

                serviceInformation.setServiceId(serviceId);
                appointment.setServicesInformation(List.of(serviceInformation));
                user.setAppointments(List.of(appointment));
                userRepository.save(user);
                log.info("Нашёл совпадение по имени услуги и записал в сущность и сохранил в юзера {}",
                        serviceInformation.toString());
                return serviceId;
            }
        }
        log.warn("Услуга не найдена: {}", serviceName);
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

        log.info("Staff Data - конвертированный лист с ДТО с данными о работниках: " + employeeDTOList);
        return employeeDTOList;
    }
    @Tool("""
            Если пользователь точно выбрал и подтвердил к какому сотруднику он намерен пойти.
            Пойми из контекста твёрдость решения пользователя. Можно задать уточняющий вопрос.
            Передай в метод id этого сотрудника в поле {{staffId}}
            """)
    public void confirmationOfEmployeeSelection(String staffId) {
        if (staffId != null && !staffId.isEmpty()) {
            appointment.setStaffId(staffId);
            appointmentsRepository.save(appointment);
            log.info("сохранил в БД обновленную запись с id сотрудника {}", appointment.toString());
        } else {
            log.warn("Сотрудник с таким id не найден: {}", staffId);
            throw new NotFoundException("Сотрудник с таким id не найден");
        }

//        if (staffName == null || staffName.isEmpty()) {
//            throw new NotFoundException("В тул с записью выбранного сотрудника в БД LLM передала пустое имя");
//        }
//
//        String response = yClientService.getListEmployeesAvailableForBooking(
//                null, null
//        ).block();
//        List<EmployeeDTO> employeeDTOList = employeeMapper.mapJsonToEmployeeList(response);
//        for (EmployeeDTO employeeDTO : employeeDTOList) {
//            if (employeeDTO.getName().toLowerCase().contains(staffName.toLowerCase())) {
//                String staffId = employeeDTO.getId();
//                appointment.setStaffId(staffId);
//                appointmentsRepository.save(appointment);
//                log.info("сохранил в БД обновленную запись с id сотрудника {}", appointment.toString());
//            }
//            log.warn("Сотрудник с таким именем не найден: {}", staffName);
//            throw new NotFoundException("Сотрудник с таким названием не найден");

//        Передай в метод полное имя выбранного сотрудника, например 'хочу к Кристине Сергеевне', передай строку 'Кристина Сергеевна'
//        }
    }

    @Tool("""
            Сейчас клиент интересуется самым ближайшим временем, когда можно забронировать.
            Клиент уже назвал желаемую услугу и выбрал специалиста. Передай в метод выбранные им id услуги {{serviceId}}
            и id сотрудника {{staffId}}
            В ответ ты получишь список с данными по свободным сеансам для брони(дату, время и длину сеанса) 
            - выведи их клиенту.
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
                availableSessionMapper.mapJsonToAvailableSessionList(response);
        log.info("Получил из мапера лист с доступными сеансами для брони {}",
                availableSessionDTOS.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
        return availableSessionDTOS;
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
}
