package com.ivan_degtev.whatsappbotforneoderma.component;

import com.ivan_degtev.whatsappbotforneoderma.controller.YClientController;
import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final YClientController yClientController;
    private final ApplicationEventPublisher eventPublisher;
    private final JsonLoggingService jsonLogging;

    DailyScheduler(
            YClientController yClientController,
            EmployeeMapper employeeMapper,
            ServiceMapper serviceMapper,
            ApplicationEventPublisher eventPublisher,
            JsonLoggingService jsonLogging
    ) {
        this.yClientController = yClientController;
        this.employeeMapper = employeeMapper;
        this.serviceMapper = serviceMapper;
        this.eventPublisher = eventPublisher;
        this.jsonLogging = jsonLogging;
    }

    private  List<EmployeeDTO> employeeDTOList;
    private  List<ServiceInformationDTO> serviceInformationDTOList;

    /**
     * Шедлер работает 1 раз в сутки для обновления первичных данных от ЯКлиента - имен сотрудников, названий процедур
     * и всех этих id, для создания более точечных запросов
     * Временно хранит в локал переменной класса
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyTasks() {
        log.info("DailyScheduler started");

        Mono<String> staffMono = yClientController.getListEmployeesAvailableForBooking(null, null);
        staffMono.subscribe(response -> {
            employeeDTOList = employeeMapper.mapJsonToEmployeeList(response);
            jsonLogging.info("Staff Data - конвертированный лист с ДТО с данными о работниках: {}", employeeDTOList);
        }, error -> jsonLogging.error("Failed to fetch staff data: {}", error.getMessage()));

        Mono<String> servicesMono = yClientController.getListServicesAvailableForBooking(null, null, null);
        servicesMono.subscribe(response -> {
            serviceInformationDTOList = serviceMapper.mapJsonToServiceList(response);
            jsonLogging.info("Services Data конвертированный лист с ДТО с данными об услугах: {}",
                    serviceInformationDTOList);
        }, error -> jsonLogging.error("Failed to fetch services data: {}", error.getMessage()));

    }

    @PostConstruct
    public void init() {
        scheduleDailyTasks();
    }
}

