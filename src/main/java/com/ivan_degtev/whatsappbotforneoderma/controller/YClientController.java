package com.ivan_degtev.whatsappbotforneoderma.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.tests.BookingRequestDto;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.MessageService;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient.YClientSendServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient.YClientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/yclients")
public class YClientController {
    private final MessageService messageService;

    @Value("https://api.yclients.com/api/v1/")
    private final String YClientUrl;
    private final Long companyId = 316398L;

    @Value("${yclient.token}")
    private final String yClientToken;

    private final WebClient webClient;
    private final YClientServiceImpl yclientService;
    private final YClientSendServiceImpl yClientSendService;

    public YClientController(
            MessageService messageService,
            @Value("https://api.yclients.com/api/v1")
            String YClientUrl,
            @Value("${yclient.token}")
            String yClientToken,
            WebClient.Builder webClientBuilder,
            YClientServiceImpl yClientService,
            YClientSendServiceImpl yClientSendService
    ) {
        this.messageService = messageService;
        this.YClientUrl = YClientUrl;
        this.yClientToken = yClientToken;
        this.webClient = webClientBuilder.baseUrl("https://api.yclients.com/api/v1")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.api.v2+json")
                .build();
        this.yclientService = yClientService;
        this.yClientSendService = yClientSendService;
    }

    /**
     * Просто поулчить все даты когда можно записаться хоть куда то
     */
    @GetMapping(path = "/book_dates")
    public Mono<String> getListDatesAvailableForBooking(
            @RequestParam(name = "serviceIds", required = false) List<String> serviceIds
    ) {
        return yclientService.getListDatesAvailableForBooking(serviceIds);
    }


    /**
     * @return JSON с уже занятыми часами у данного сотрудника
     */
    @GetMapping(path = "/book_services")
    public Mono<String> getListServicesAvailableForBooking(
            @RequestParam(name = "staffId", required = false) Long staffId,
            @RequestParam(name = "datetime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime,
            @RequestParam(name = "serviceIds", required = false) List<Long> serviceIds
    ) {
        return yclientService.getListServicesAvailableForBooking(staffId, datetime, serviceIds);
    }


    @GetMapping(path = "/book_staff_seances")
    public Mono<String> getListNearestAvailableSessions(
            @RequestParam(name = "staffId") Long staffId,
            @RequestParam(name = "service_ids", required = false) List<String> serviceIds
    ) {
        return yclientService.getListNearestAvailableSessions(staffId, serviceIds);
    }

    /**
     * Эта ручка используется когда нужно подобрать сотрудника - показывает всех, кто может оказать указанную(!) услугу(id)
     */
    @GetMapping(path = "/book_staff")
    public Mono<String> getListEmployeesAvailableForBooking(
            @RequestParam(name = "service_ids", required = false) List<String> serviceIds,
            @RequestParam(name = "datetime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime
    ) {
        return yclientService.getListEmployeesAvailableForBooking(serviceIds, datetime);
    }

    @GetMapping(path = "/book_times")
    public Mono<String> getListSessionsAvailableForBooking(
            @RequestParam(name = "date") String date,
            @RequestParam(name = "staffId") Long staffId,
            @RequestParam(name = "serviceIds", required = false) List<String> serviceIds
    ) throws JsonProcessingException {
        return yclientService.getListSessionsAvailableForBooking(date, staffId, serviceIds);
    }

    /**
     * Тестовая ручка, для отправки записей через постман при обращении к localhost, нужна для отладки
     * @param bookingRequestDto
     * @return
     */
    @PostMapping("/book")
    public ResponseEntity<String> sendBookingRequest(@RequestBody BookingRequestDto bookingRequestDto) {
        try {
            yClientSendService.sendBookingRequest(bookingRequestDto)
                    .doOnSuccess(response -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            log.info("Успешно отправлен запрос: {}", response.getBody());
                        } else {
                            log.warn("Ошибка при отправке запроса: {}", response.getBody());
                        }
                    })
                    .block();
            return ResponseEntity.ok("Запрос успешно отправлен");
        } catch (Exception e) {
            log.error("Ошибка при отправке запроса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при отправке запроса: " + e.getMessage());
        }
    }
}
