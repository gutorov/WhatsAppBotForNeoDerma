package com.ivan_degtev.whatsappbotforneoderma.service.impl;

import com.ivan_degtev.whatsappbotforneoderma.service.YClientService;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@Getter
public class YClientServiceImpl implements YClientService {

    private final WebClient webClient;
    private final JsonLoggingService jsonLogging;
    private final JsonLoggingService jsonLoggingService;

    @Value("${yclient.token}")
    private String yClientToken;
    private final Long companyId = 316398L;

    public YClientServiceImpl(
            WebClient.Builder webClientBuilder,
            JsonLoggingService jsonLogging,
            JsonLoggingService jsonLoggingService
    ) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.yclients.com/api/v1")
                .build();
        this.jsonLogging = jsonLogging;
        this.jsonLoggingService = jsonLoggingService;
    }



    /**
     * Выводит список доступных дат(в разных форматах) для брони.
     * Общая инфа, не уверен, что в бизнес-логике будет использоваться, нужно более конкретная фильтрация
     */
    @Override
    public Mono<String> getListDatesAvailableForBooking(
            List<String> serviceIds
    ) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/book_dates/{company_id}");
                    if (serviceIds != null && !serviceIds.isEmpty()) {
                        uriBuilder.queryParam("service_ids[]", serviceIds);
                    }
                    return uriBuilder
                            .build(companyId);
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yClientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> jsonLoggingService.info("Response from Yclients: {}", response))
                .doOnError(error -> jsonLogging.error("Failed to fetch dates: {}", error.getMessage()));
    }


    /**
     * Здесь получаем все виды услуг.
     * Для ЛЛМ важно добавлять в фильтр айди сотрудника и дату, чтоб получить АКТУАЛЬНЫЕ УСЛУГИ в этой конфигурации
     * Добавлять айди услуги бессмысленно - это какой-тто невнятный фильтр
     * ВАЖНО!
     * Для получения полной инфы об услугах - делать запрос только с указанием staff_id
     * если указать еще и дату - получишь урезанную версию, где будут комплексы услуг, только с id категории без id услуги!
     */
    @Override
    public Mono<String> getListServicesAvailableForBooking(
            Long staffId,
            LocalDateTime datetime,
            List<Long> serviceIds
    ) {
        return webClient.get()
                .uri(uriBuilder -> {
                    String path = "/book_services/{company_id}";

                    uriBuilder.path(path).queryParam("staff_id", staffId)
                            .queryParam("datetime", datetime != null ? datetime.toString() : null);
                    if (serviceIds != null && !serviceIds.isEmpty()) {
                        for (Long serviceId : serviceIds) {
                            uriBuilder.queryParam("serviceIds", serviceId);
                        }
                    }

                    return uriBuilder.build(companyId);
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yClientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> jsonLogging.info("Response from Yclients: {}", response))
                .doOnError(error -> jsonLogging.error("Failed to fetch services: {}", error.getMessage()));
    }

    /**
     * Метод показывает ближайший доступный сеанс по заданному сотруднику.
     * Принимает id компании(константа) и id сотруника(пока захардкожен - ИЗМЕНИТЬ)
     * По факту возвращаются данные о ближайщем дне, где есть окна
     * (схоже с методом getListSessionsAvailableForBooking, но без указания конкретной даты - берётся сама близкая)
     * ДЛя работы с LLM нужно дать доп инфу о
     * часах работы, длине различных процедур,
     * чтоб LLM делала выводы о возможности записи
     *
     */
    @Override
    public Mono<String> getListNearestAvailableSessions(
            Long staffId,
            List<String> serviceIds
    ) {
        return webClient.get()
                .uri(uriBuilder -> {
                    StringBuilder pathBuilder = new StringBuilder("/book_staff_seances/{company_id}/{staff_id}");

                    if (serviceIds != null && !serviceIds.isEmpty()) {
                        uriBuilder.queryParam("service_ids", serviceIds.toArray());
                    }

                    return uriBuilder
                            .path(pathBuilder.toString())
                            .build(companyId, staffId);
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yClientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> jsonLogging.info("Response from Yclients: {}", response))
                .doOnError(error -> jsonLogging.error("Failed to fetch services: {}", error.getMessage()));
    }

    /**
     * Метод выдает данные о каждом сотруднике, без учета их времени, только личная информация
     * ВАЖНО! Отсюда брать описание и ID!!
     */
    @Override
    public Mono<String> getListEmployeesAvailableForBooking(
            List<String> serviceIds,
            LocalDateTime datetime
    ) {
        return webClient.get()
                .uri(uriBuilder -> {
                    StringBuilder pathBuilder = new StringBuilder("/book_staff/{company_id}");
                    if (serviceIds != null && !serviceIds.isEmpty()) {
                        uriBuilder.queryParam("service_ids", serviceIds.toArray());
                    }
                    if (datetime != null) {
                        uriBuilder.queryParam("datetime", datetime.format(DateTimeFormatter.ISO_DATE_TIME));
                    }

                    return uriBuilder
                            .path(pathBuilder.toString())
                            .build(companyId);
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yClientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> jsonLogging.info("Response from Yclients: {}", response))
                .doOnError(error -> jsonLogging.error("Failed to fetch employees: {}", error.getMessage()));
    }

    /**
     * Метод проверяет доступен ли для записи ОПРЕДЕЛЁНННЫЙ СОТРУДНИК на ОПРЕДЕЛЁННУЮ ДАТУ
     * Два этих параметра передавать. Может получать 404 ответ - значит сотрудник недоуступен.
     * Время в ответе от яклиента указано в секундах!
     */
    @Override
    public Mono<String> getListSessionsAvailableForBooking(
            String date,
            Long staffId,
            List<String> serviceIds
    ) {
        return webClient.get()
                .uri(uriBuilder -> {
                    StringBuilder pathBuilder = new StringBuilder("/book_times/{company_id}");
                    if (staffId != null) {
                        pathBuilder.append("/{staff_id}");
                    }
                    if (date != null && !date.isEmpty()) {
                        pathBuilder.append("/{date}");
                    }
                    if (serviceIds != null && !serviceIds.isEmpty()) {
                        uriBuilder.queryParam("service_ids", serviceIds.toArray());
                    }
                    return uriBuilder
                            .path(pathBuilder.toString())
                            .build(
                                    companyId,
                                    staffId != null ? staffId : "",
                                    date != null ? date : ""
                            );
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yClientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> jsonLogging.info("Response from Yclients: {}", response))
                .doOnError(error -> jsonLogging.error("Failed to fetch sessions: {}", error.getMessage()));
    }

}