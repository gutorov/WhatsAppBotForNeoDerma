package com.ivan_degtev.whatsappbotforneoderma.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface YClientService {
    Mono<String> getListDatesAvailableForBooking(Long companyId);
    Mono<String> getListServicesAvailableForBooking(
            Long companyId,
            Long staffId,
            LocalDateTime datetime,
            List<Long> serviceIds
    );
    Mono<String> getListNearestAvailableSessions(Long companyId);
    Mono<String> getListEmployeesAvailableForBooking(Long companyId);
    Mono<String> getListSessionsAvailableForBooking(Long companyId);

}
