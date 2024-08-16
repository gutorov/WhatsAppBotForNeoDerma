package com.ivan_degtev.whatsappbotforneoderma.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.MessageService;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.YClientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class YClientController {
    private final MessageService messageService;

    @Value("https://api.yclients.com/api/v1/")
    private final String YClientUrl;

    @Value("${yclient.token}")
    private final String yclientToken;
    private final WebClient webClient;
    private final YClientServiceImpl yclientService;
    private final Long companyId = 316398L;

    public YClientController(
            MessageService messageService,
            @Value("https://api.yclients.com/api/v1")
            String YClientUrl,
            @Value("${yclient.token}")
            String yclientToken,
            WebClient.Builder webClientBuilder,
            YClientServiceImpl yclientService
    ) {
        this.messageService = messageService;
        this.YClientUrl = YClientUrl;
        this.yclientToken = yclientToken;
        this.webClient = webClientBuilder.baseUrl("https://api.yclients.com/api/v1")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .build();
        this.yclientService = yclientService;
    }

    @GetMapping(path = "/book_dates")
    public Mono<String> getListDatesAvailableForBooking() {
        return yclientService.getListDatesAvailableForBooking(companyId);
    }


    /**
     *
     * @param staffId
     * @param datetime
     * @param serviceIds
     * @return JSON с уже занятыми часами у данного сотрудника
     */
    @GetMapping(path = "/book_services")
    public Mono<String> getListServicesAvailableForBooking(
            @RequestParam(name = "staffId", required = false) Long staffId,
            @RequestParam(name = "datetime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime,
            @RequestParam(name = "serviceIds", required = false) List<Long> serviceIds
    ) {
        return yclientService.getListServicesAvailableForBooking(companyId, staffId, datetime, serviceIds);
    }


    @GetMapping(path = "/book_staff_seances")
    public Mono<String> getListNearestAvailableSessions() {
        return yclientService.getListNearestAvailableSessions(companyId);
    }

    @GetMapping(path = "/book_staff")
    public Mono<String> getListEmployeesAvailableForBooking() {
        return yclientService.getListEmployeesAvailableForBooking(companyId);
    }

    @GetMapping(path = "/book_times")
    public Mono<String> getListSessionsAvailableForBooking() throws JsonProcessingException {
        return yclientService.getListSessionsAvailableForBooking(companyId);
    }


}
