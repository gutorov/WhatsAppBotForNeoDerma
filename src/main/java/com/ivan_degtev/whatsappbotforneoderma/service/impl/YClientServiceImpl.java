package com.ivan_degtev.whatsappbotforneoderma.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ivan_degtev.whatsappbotforneoderma.service.YClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class YClientServiceImpl implements YClientService {

    private final WebClient webClient;

    @Value("${yclient.token}")
    private String yclientToken;

    public YClientServiceImpl(
            WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder.baseUrl("https://api.yclients.com/api/v1").build();
    }

    /**
     * Выводит список доступных дат(в разных форматах) для брони.
     * Общая инфа, не уверен, что в бизнес-логике будет использоваться, нужно более конкретная фильтрация
     */
    @Override
    public Mono<String> getListDatesAvailableForBooking(Long companyId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/book_dates/{company_id}")
                        .build(companyId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yclientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Response from Yclients: " + response))
                .doOnError(error -> System.err.println("Failed to fetch services: " + error.getMessage()));
    }

    /**
     * Здесь получаем все виды услуг.
     * Если добавить(пока нет) фильтр по сотруднику - получим продолжительность услуги(ВАЖНО),
     * если указать дату - будет фильтр доступных услуг.
     * В LLM нужно будет использовать этот метод для поиска
     */
    @Override
    public Mono<String> getListServicesAvailableForBooking(Long companyId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/book_services/{company_id}")
                        .build(companyId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yclientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Response from Yclients: " + response))
                .doOnError(error -> System.err.println("Failed to fetch services: " + error.getMessage()));
    }

    /**
     * Метод показывает ближайший доступный сеанс по заданному сотруднику.
     * Принимает id компании(константа) и id сотруника(пока захардкожен - ИЗМЕНИТЬ)
     * По факту возвращаются данные о ближайщем дне, где есть окна, но указаны именно занятые слоты
     * (схоже с методом getListSessionsAvailableForBooking, но без указания конкретной даты - берётся сама близкая)
     * ДЛя работы с LLM нужно дать доп инфу о
     * часах работы, длине различных процедур,
     * чтоб LLM делала выводы о возможности записи
     *
     */
    @Override
    public Mono<String> getListNearestAvailableSessions(Long companyId) {
        Long staffId = 1592958L;
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/book_staff_seances/{company_id}/{staff_id}")
                        .build(companyId, staffId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yclientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Response from Yclients: " + response))
                .doOnError(error -> System.err.println("Failed to fetch services: " + error.getMessage()));
    }

    /**
     * Метод выдает данные о каждом сотруднике, без учета их времени, только личная информация
     * ВАЖНО! Отсюда брать описание и ID!!
     */
    @Override
    public Mono<String> getListEmployeesAvailableForBooking(Long companyId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/book_staff/{company_id}")
                        .build(companyId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yclientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Response from Yclients: " + response))
                .doOnError(error -> System.err.println("Failed to fetch services: " + error.getMessage()));
    }

    /**
     * Метод проверяет доступен ли для записи ОПРЕДЕЛЁНННЫЙ СОТРУДНИК на ОПРЕДЕЛЁННУЮ ДАТУ
     * Два этих параметра передавать. Может получать 404 ответ - значит сотрудник недоуступен.
     * Время в ответе от яклиента указано в секундах!
     */
    @Override
    public Mono<String> getListSessionsAvailableForBooking(Long companyId) {
        Long staffId = 1592958L;
        String date = "2024-08-16";
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/book_times/{company_id}/{staff_id}/{date}")
                        .build(companyId, staffId, date))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + yclientToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Response from Yclients: " + response))
                .doOnError(error -> System.err.println("Failed to fetch services: " + error.getMessage()));
    }
}