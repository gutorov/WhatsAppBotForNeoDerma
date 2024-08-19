package com.ivan_degtev.whatsappbotforneoderma.service.ai;

import com.ivan_degtev.whatsappbotforneoderma.component.DailyScheduler;
import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.AIAnalyzer;
import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.Assistant;
import com.ivan_degtev.whatsappbotforneoderma.controller.YClientController;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.AvailableStaffForBookingService;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.DataForWriteDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.AppointmentsRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@org.springframework.stereotype.Service
@Slf4j
@AllArgsConstructor
@DependsOn("dailyScheduler")
public class LangChain4jService {
    private Assistant assistant;
    private AIAnalyzer aiAnalyzer;
    private DailyScheduler dailyScheduler;
    private final YClientController yClientController;
    private final EmployeeMapper employeeMapper;
    private final UserRepository userRepository;
    private final ServiceInformationRepository serviceInformationRepository;
    private final AppointmentsRepository appointmentsRepository;

    public void testLLM() {
        log.info("testLLM tarted - тестим приход из вотсапа по хуку ");

        User user = new User();
        user.setChatId("111");
        user.setSenderPhoneNumber("79951489346");
        Appointment appointment = new Appointment();
        ServiceInformation serviceInformation = new ServiceInformation();
        appointment.setServicesInformation(List.of(serviceInformation));
        user.setAppointments(List.of(appointment));
        userRepository.save(user);

        DataForWriteDTO dataForWriteDTO = new DataForWriteDTO();

        while(true) {
            Scanner scanner = new Scanner(System.in);
            String question = scanner.nextLine();

            if (question.equals("exit")) {
                scanner.close();
            }
            langChain4JMainModule(
                    question,
                    user,
                    appointment,
                    dataForWriteDTO
            );
        }
    }
    public void langChain4JMainModule(
            String question,
            User currentUser,
            Appointment appointment,
            DataForWriteDTO dataForWriteDTO
    ) {
        log.info("LangChain4jMainModule started");

        String availableDatesForBookingServices;

        String analyzedAnswer = aiAnalyzer.chat(question);
        log.info("Анализатор определит тип вопроса, как: " + analyzedAnswer);

        switch (analyzedAnswer) {
            case "1" -> {


                String assistantAnswer = assistant.greeting(
                        question
                );
                log.info("Ответ: " + assistantAnswer);


            }
            case "2" -> {

//                String name = assistant.writeUserName(
//                        dataForWriteDTO,
//                        question
//                );


                List<ServiceInformation> serviceInformationList = dailyScheduler.getServicesInformationList();
                String assistantAnswer = assistant.onlyServiceDialog(
                        serviceInformationList,
                        question
                );
                log.info("Ответ: " + assistantAnswer);


            }
            case "3" -> {
                List<ServiceInformation> serviceInformationList = serviceInformationRepository.findAll();
                String encodedServiceId = assistant.searchFreeDatesByNameService(
                        serviceInformationList,
                        question
                ); //по идее отдаёт только 1 строку - айди услуги - мапим далее в лист
                List<Long> serviceIdsFromUtilAIMethod = List.of(Long.valueOf(encodedServiceId));
                log.info("утилитный метод получил айди предполагаемой услуги и ее класс - первый елемент листа - {}",
                        serviceInformationList.get(0).toString());

                List<ServiceInformation> actualServicesForUser
                        = serviceInformationRepository.findByServiceIdIn(serviceIdsFromUtilAIMethod);
                appointment.setServicesInformation(actualServicesForUser);

                currentUser.setAppointments(List.of(appointment));

                //нужно преобразовать лист лонгов-внешних айди услуг в лист строк, для поиска по внешнему апи
                // актуальных свободных дат
                List<String> serviceIdsStringList = serviceIdsFromUtilAIMethod
                        .stream()
                        .map(String::valueOf)
                        .toList();

                availableDatesForBookingServices =
                        yClientController.getListDatesAvailableForBooking(serviceIdsStringList).block();
                log.info("Получил моно строку json от запроса к яклиенту {}", availableDatesForBookingServices);

                if (availableDatesForBookingServices == null || availableDatesForBookingServices.isEmpty()) {
                    throw new IllegalArgumentException("responseWithFreeDates is missing or empty");
                }
                String assistantAnswer = assistant.onlyService(
                        availableDatesForBookingServices,
                        question
                );
                log.info("Ответ: " + assistantAnswer);


            }
            case "4" -> {

                String specifiedDate = assistant.addingDateForAppointment(
                        question
                );
                appointment.setDatetime(LocalDateTime.parse(specifiedDate));
                log.info("дата из запроса юзера конвертировалась в поле в  appointment {}", appointment);

                List<String> serviceIds = appointment.getServicesInformation()
                        .stream()
                        .map(ServiceInformation::getServiceId)
                        .toList();

                // получаем  от внешнего апи - данные о сотрудниках, которые могут оказать данную услугу в данную дату
                // без времени - ответ будет неполным и почти бесполезным
                String json = yClientController.getListEmployeesAvailableForBooking(
                        serviceIds,
                        appointment.getDatetime()
                ).block();
                List<AvailableStaffForBookingService> freeEmployeesForThisService =
                        employeeMapper.mapJsonToAvailableStaffForBookingService(json);
                String assistantAnswer = assistant.onlyServiceAndDate(
                        freeEmployeesForThisService,
                        question
                );

                log.info("Ответ: {}", assistantAnswer);


            }
            case "0" -> log.info("Миша, давай по новой");
        }
    }
    }

