package com.ivan_degtev.whatsappbotforneoderma.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface YClientService {
    Mono<String> getListDatesAvailableForBooking(Long companyId);
    Mono<String> getListServicesAvailableForBooking(Long companyId);
    Mono<String> getListNearestAvailableSessions(Long companyId);
    Mono<String> getListEmployeesAvailableForBooking(Long companyId);
    Mono<String> getListSessionsAvailableForBooking(Long companyId);

}
