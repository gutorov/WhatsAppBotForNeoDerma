package com.ivan_degtev.whatsappbotforneoderma.component;

import com.ivan_degtev.whatsappbotforneoderma.controller.YClientController;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
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

    DailyScheduler(
            YClientController yClientController,
            EmployeeMapper employeeMapper,
            ServiceMapper serviceMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.yClientController = yClientController;
        this.employeeMapper = employeeMapper;
        this.serviceMapper = serviceMapper;
        this.eventPublisher = eventPublisher;
    }

    private  List<EmployeeDTO> employeeDTOList;
    private  List<ServiceInformation> servicesInformationList;

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
            log.info("Staff Data - конвертированный лист с ДТО с данными о работниках: " + employeeDTOList);
        }, error -> log.error("Failed to fetch staff data: " + error.getMessage()));

        Mono<String> servicesMono = yClientController.getListServicesAvailableForBooking(null, null, null);
        servicesMono.subscribe(response -> {
            servicesInformationList = serviceMapper.mapJsonToServiceList(response);
            log.info("Services Data конвертированный лист с ДТО с данными об услугах: " + servicesInformationList);
        }, error -> log.error("Failed to fetch services data: " + error.getMessage()));


    }

    @PostConstruct
    public void init() {
        scheduleDailyTasks();
    }
}

