package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.exception.NotFoundException;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import dev.langchain4j.agent.tool.Tool;
import jdk.jfr.Label;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class Tools {
    private final ServiceMapper serviceMapper;

    private static final String yclientToken = System.getenv("yclient.token");
    private final WebClient webClient = WebClient.builder().build();
    private static final Long companyId = 316398L;
    private static List<EmployeeDTO> employees;
    private  static List<ServiceInformation> servicesInformationList;


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
    public List<ServiceInformation> getServicesInformation() {
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
        return servicesInformationList = serviceMapper.mapJsonToServiceList(response);
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
    public List<ServiceInformation> getAllServices(String question) {
        List<ServiceInformation> serviceInformationList = getServicesInformation();
        log.info("Тул нашел id услуги {}", serviceInformationList);
        return serviceInformationList;
    }

    @Tool("""
    По запросу пользователя найди услугу и верни её serviceId из поля serviceId.
    Например, если пользователь говорит "запишите меня на стрижку", найди ID услуги "стрижка".
    """)
    public String getIdService(String serviceName) {
        List<ServiceInformation> serviceInformationList = getServicesInformation();

        for (ServiceInformation service : serviceInformationList) {
            if (service.getTitle().toLowerCase().contains(serviceName.toLowerCase())) {
                return service.getServiceId();
            }
        }
        log.warn("Услуга не найдена: {}", serviceName);
        throw new NotFoundException("Услуга с таким названием не найдена");
    }
}
