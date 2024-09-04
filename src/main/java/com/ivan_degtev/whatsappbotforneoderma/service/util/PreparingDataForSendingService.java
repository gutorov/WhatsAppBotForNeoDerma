package com.ivan_degtev.whatsappbotforneoderma.service.util;

import com.ivan_degtev.whatsappbotforneoderma.controller.AmoCrmController;
import com.ivan_degtev.whatsappbotforneoderma.dto.amoCrm.CustomFieldDto;
import com.ivan_degtev.whatsappbotforneoderma.dto.amoCrm.LeadDto;
import com.ivan_degtev.whatsappbotforneoderma.dto.amoCrm.ValueDto;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.AppointmentsRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient.YClientSendServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Утилитный класс - конмопнует данные и отправляет в яклиент и амосрм. Статические поля можно проверять через AmoCrmController - нужными запросами
 */
@Service
@Slf4j
@AllArgsConstructor
public class PreparingDataForSendingService {

    private final YClientSendServiceImpl yClientSendService;
    private final AppointmentsRepository appointmentsRepository;
    private final ServiceInformationRepository serviceInformationRepository;
    private final AmoCrmController amoCrmController;

    //Колонка - "лиды от ИИ" - для мониторинга наших заявок
    private final static Integer AMOCRM_PIPELINE_ID = 6373406;
    //Статус - в работе
    private final static Integer AMOCRM_STATUS_ID = 69559066;
    //Статус - поле заполнено роботом
    private final static Integer AMOCRM_CREATED_BY = 0;
    //ID для любых кастомных полей - принимаает текст
    private final static Integer AMOCRM_NUMBER_FOR_CUSTOM_FIELDS = 1881929;

    public void sendingAndCheckToYclientService(
            User currentUser,
            Appointment currentAppointment
    ) {
        List<Appointment> currentAppointments = List.of(currentAppointment);
        List<ServiceInformation> currentServiceInformation =
                currentAppointment.getServicesInformation();

        /*
        Отправка пост запроса на Yclient с записью
         */
        yClientSendService.sendBookingRequest(
                        currentUser,
                        currentAppointments,
                        currentServiceInformation
                )
                .subscribe(responseEntity -> {
                    Map<String, Object> responseBody = responseEntity.getBody();
                    if (responseBody != null) {
                        boolean success = (Boolean) responseBody.get("success");
                        if (success) {
                            log.info("Запись прошла успешно.");
                            addingFinalFlagAboutSuccessfulRecording(currentAppointments);

                            createRecordInAmoCrm(currentUser, currentAppointment);
                        } else {
                            log.warn("Запись не удалась.");
                        }
                    }
                });
    }

    public void createRecordInAmoCrm(User currentUser, Appointment currentAppointment) {

        List<ServiceInformation> currentServiceInformation = serviceInformationRepository.findAllByAppointment(currentAppointment);

        LeadDto leadDto = new LeadDto();
        leadDto.setName(currentServiceInformation.get(0).getTitle());
        leadDto.setPipelineId(AMOCRM_PIPELINE_ID);
        leadDto.setCreatedBy(AMOCRM_CREATED_BY);
        leadDto.setPrice(Integer.valueOf(currentServiceInformation.get(0).getPriceMax()));
        leadDto.setStatusId(AMOCRM_STATUS_ID);

        List<CustomFieldDto> customFields = new ArrayList<>();

        CustomFieldDto nameField = new CustomFieldDto();
        nameField.setFieldId(AMOCRM_NUMBER_FOR_CUSTOM_FIELDS);
        nameField.setValues(List.of(new ValueDto(currentUser.getSenderName())));

        CustomFieldDto phoneField = new CustomFieldDto();
        phoneField.setFieldId(AMOCRM_NUMBER_FOR_CUSTOM_FIELDS);
        phoneField.setValues(List.of(new ValueDto(currentUser.getSenderPhoneNumber())));

        CustomFieldDto dateField = new CustomFieldDto();
        dateField.setFieldId(AMOCRM_NUMBER_FOR_CUSTOM_FIELDS);
        dateField.setValues(List.of(new ValueDto(currentAppointment.getDatetime().toString())));

        customFields.add(nameField);
        customFields.add(phoneField);
        customFields.add(dateField);

        leadDto.setCustomFieldsValues(customFields);

        amoCrmController.addLead(leadDto);
    }

    /**
     * Добавляет в appointments флаг по отправке запрос на добавление записи в яклиент.
     * Финальное изменение
     * @param currentAppointments
     */
    @Transactional
    public void addingFinalFlagAboutSuccessfulRecording(List<Appointment> currentAppointments) {
        if (currentAppointments == null || currentAppointments.isEmpty()) {
            log.warn("Список встреч пуст или не задан.");
            return;
        }

        currentAppointments.stream()
                .filter(Objects::nonNull)
                .peek(appointment -> {
                    appointment.setApplicationSent(true);
                    appointmentsRepository.save(appointment);
                })
                .forEach(appointment -> log.info("Обновлена встреча с id: {}", appointment.getId()));
        log.info("Все встречи были обновлены с applicationSent=true.");
    }
}
