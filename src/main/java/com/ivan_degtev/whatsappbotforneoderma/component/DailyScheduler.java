package com.ivan_degtev.whatsappbotforneoderma.component;

import com.ivan_degtev.whatsappbotforneoderma.controller.YClientController;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.ServiceDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@Getter
public class DailyScheduler {

    private final EmployeeMapper employeeMapper;
    private final ServiceMapper serviceMapper;

    private YClientController yClientController;
    DailyScheduler(
            YClientController yClientController,
            EmployeeMapper employeeMapper,
            ServiceMapper serviceMapper
    ) {
        this.yClientController = yClientController;
        this.employeeMapper = employeeMapper;
        this.serviceMapper = serviceMapper;
    }

    private static List<EmployeeDTO> employeeDTOList;
    private static List<ServiceDTO> servicesDTOList;

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyTasks() {
        log.info("DailyScheduler started");

        Mono<String> staffMono = yClientController.getListEmployeesAvailableForBooking();
        staffMono.subscribe(response -> {
            employeeDTOList = employeeMapper.mapJsonToEmployeeList(response);
            log.info("Staff Data: " + employeeDTOList);
        }, error -> log.error("Failed to fetch staff data: " + error.getMessage()));

        Mono<String> servicesMono = yClientController.getListServicesAvailableForBooking();
        servicesMono.subscribe(response -> {
            servicesDTOList = serviceMapper.mapJsonToServiceList(response);
            log.info("Services Data: " + servicesDTOList);
        }, error -> log.error("Failed to fetch services data: " + error.getMessage()));
    }

    @PostConstruct
    public void init() {
        scheduleDailyTasks();
    }
}

