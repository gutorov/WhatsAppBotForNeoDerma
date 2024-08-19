package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.AppointmentsRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import dev.langchain4j.agent.tool.Tool;
import jdk.jfr.Label;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Tools {
    private final ServiceMapper serviceMapper;
//    private final EmployeeMapper employeeMapper;
    private final WebClient webClient = WebClient.builder().build();
    private final ServiceInformationRepository serviceInformationRepository;
    private final AppointmentsRepository appointmentsRepository;
    private final UserRepository userRepository;
    private User user;


    private static final String yclientToken = System.getenv("yclient.token");
    private static final Long companyId = 316398L;
    private static List<EmployeeDTO> employees;
    private  static List<ServiceInformationDTO> servicesInformationDTOList;

    public Tools(
            ServiceMapper serviceMapper,
//            EmployeeMapper employeeMapper,
            ServiceInformationRepository serviceInformationRepository,
            AppointmentsRepository appointmentsRepository,
            UserRepository userRepository,
            User user
    ) {
        this.serviceMapper = serviceMapper;
//        this.employeeMapper = employeeMapper;
        this.serviceInformationRepository = serviceInformationRepository;
        this.appointmentsRepository = appointmentsRepository;
        this.userRepository = userRepository;
        this.user = user;
    }

    public String getListDatesAvailableForBooking() {
        String response = webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder
                            .scheme("https")
                            .host("api.yclients.com")
                            .path("/api/v1/book_dates/{company_id}");
                    return builder.build(companyId);
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yclientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;
    }

    /**
     * Просто инфа о всех сервисах
     */
    public List<ServiceInformationDTO> getServicesInformation() {
        String response =  webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder
                            .scheme("https")
                            .host("api.yclients.com")
                            .path("/api/v1/book_services/{company_id}");
                    return builder.build(companyId);
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yclientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return servicesInformationDTOList = serviceMapper.mapJsonToServiceList(response);
    }

    /**
     * Получить всех сотрудников, что выполняют услугу
     */
    public String getListEmployeesAvailableForBooking(String serviceIds) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder = uriBuilder
                            .scheme("https")
                            .host("api.yclients.com")
                            .path("/api/v1/book_staff/{company_id}");

                    if (serviceIds != null && !serviceIds.isEmpty()) {
                        uriBuilder.queryParam("service_ids", serviceIds);
                    }
                    return uriBuilder.build(companyId);
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yclientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Response from Yclients: " + response))
                .doOnError(error -> System.err.println("Failed to fetch employees: " + error.getMessage()))
                .block();
    }

    @Tool("""
            Получить свободные даты для записи и вывести в человекочитаемом формате. Если дат очень много
            вывести результат в формате "свободны от - и до - "
            """)
    public String getFreeDates(String question) {
        String freeDates = getListDatesAvailableForBooking();
        log.info("Тул нашёл свободные даты {}", freeDates);
        return freeDates;
    }
    @Tool("""
            Получить List со всеми услугами, в этом листе есть базовая информация - названия услуг и их внутренний id
            для дальнейшего поиска. Исходя из запроса клиента - выведи внутренний id услуги, которая ему нужна.
            """)
    public List<ServiceInformationDTO> getAllServices(String question) {
        List<ServiceInformationDTO> serviceInformationList = getServicesInformation();
        log.info("Тул нашел id услуги {}", serviceInformationList);
        return serviceInformationList;
    }

    @Tool("""
    По запросу пользователя найди услугу и запиши её serviceId в объект.
    Например, если пользователь говорит "запишите меня на стрижку", найди ID услуги "стрижка".
    """)
    @Transactional
    public ServiceInformation getIdService(String serviceName) {
        List<ServiceInformationDTO> serviceInformationList = getServicesInformation();

        for (ServiceInformationDTO service : serviceInformationList) {
            if (service.getTitle().toLowerCase().contains(serviceName.toLowerCase())) {
//                return service.getServiceId();
                Appointment appointment = new Appointment();
                ServiceInformation currentServiceInformation = new ServiceInformation(); //запись на сеанс дто
                currentServiceInformation.setServiceId(service.getServiceId());
                appointment.setServicesInformation(List.of(currentServiceInformation));
                user.setAppointments(List.of(appointment));
                userRepository.save(user);
                log.info("Нашёл совпадение по имени услуги и записал в сущность и сохранил в юзера {}",
                        currentServiceInformation.toString());
                return currentServiceInformation;
            }
        }
        log.warn("Услуга не найдена: {}", serviceName);
        throw new NotFoundException("Услуга с таким названием не найдена");
    }

//    @Tool("""
//            Поиск всех сотрудников, которые оказывают выбранную клиентом услугу. Если клиент спрашивает кто может оказать
//            ему выбранную услугу - нужно найти всех и дать ему информацию о сотрудниках
//            """)
//    public List<EmployeeDTO> getListEmployeesForCurrentServices(ServiceInformation serviceInformation) {
//        String serviceId = serviceInformation.getServiceId();
//        String staffs = getListEmployeesAvailableForBooking(serviceId);
////        List<EmployeeDTO> employeeDTOList = employeeMapper.mapJsonToEmployeeList(staffs);
//        List<EmployeeDTO> employeeDTOList = new ArrayList<>(); // пустой список вместо маппинга
//
//        log.info("Staff Data - конвертированный лист с ДТО с данными о работниках: " + employeeDTOList);
//        return employeeDTOList;
//    }
}
