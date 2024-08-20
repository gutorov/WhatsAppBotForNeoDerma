package com.ivan_degtev.whatsappbotforneoderma.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface YClientService {
    Mono<String> getListDatesAvailableForBooking(
            List<String> serviceIds
    );
    Mono<String> getListServicesAvailableForBooking(
            Long staffId,
            LocalDateTime datetime,
            List<Long> serviceIds
    );
    Mono<String> getListNearestAvailableSessions(
            Long staffId,
            List<String> serviceIds
    );
    Mono<String> getListEmployeesAvailableForBooking(
            List<String> serviceIds,
            LocalDateTime datetime
    );
    Mono<String> getListSessionsAvailableForBooking(
            String date,
            Long staffId,
            List<String> serviceIds
    );

}
