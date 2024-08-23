package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.FreeSessionForBookDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.AnswerCheckMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.User;

import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;

import com.ivan_degtev.whatsappbotforneoderma.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@Service
@Slf4j
public class TestService {
    private final UserService userService;
    private final UserRepository userRepository;
    @Value("${open.ai.token}")
    private String openAiToken;
    private final AssistantTest assistantTest;

    private final AnswerCheckMapper answerCheckMapper;
    private final ObjectMapper objectMapper;
//    @Bean
//    public AssistantTest assistantTest() {
//        return AiServices.builder(AssistantTest.class)
//                .chatLanguageModel(chatLanguageModel())
//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
//                .tools(tools)
//                .build();
//    }
//
//    @Bean
//    public Tools tools(
//            YClientServiceImpl yClientService,
//            ServiceMapper serviceMapper,
//            EmployeeMapper employeeMapper,
//            AvailableSessionMapper availableSessionMapper,
//            AnswerCheckMapper answerCheckMapper,
//            ServiceInformationRepository serviceInformationRepository,
//            AppointmentsRepository appointmentsRepository,
//            UserRepository userRepository
//    ) {
//        log.info("Создали бин тулов из нового класса тулов");
//        return new Tools(
//                yClientService,
//                serviceMapper,
//                employeeMapper,
//                availableSessionMapper,
//                answerCheckMapper,
//                serviceInformationRepository,
//                appointmentsRepository,
//                userRepository
//        );
//    }



//    private final YClientServiceImpl yClientService;
//
//    private final ServiceMapper serviceMapper;
//    private final EmployeeMapper employeeMapper;
//    private final AvailableSessionMapper availableSessionMapper;
//    private final AnswerCheckMapper answerCheckMapper;
//    private final AppointmentsRepository appointmentsRepository;
//    private final ServiceInformationRepository serviceInformationRepository;

    public TestService(
            @Value("${open.ai.token}") String openAiToken,
            AssistantTest assistantTest,
//            YClientServiceImpl yClientService,
//            ServiceMapper serviceMapper,
//            EmployeeMapper employeeMapper,
//            AvailableSessionMapper availableSessionMapper,
//            AnswerCheckMapper answerCheckMapper,
//            AppointmentsRepository appointmentsRepository,
//            ServiceInformationRepository serviceInformationRepository,
//            UserRepository userRepository
            UserService userService,
            UserRepository userRepository,

            AnswerCheckMapper answerCheckMapper,
            ObjectMapper objectMapper
    ) {
        this.openAiToken = openAiToken;
        this.assistantTest = assistantTest;
//        this.yClientService = yClientService;
//        this.serviceMapper = serviceMapper;
//        this.employeeMapper = employeeMapper;
//        this.availableSessionMapper = availableSessionMapper;
//        this.answerCheckMapper = answerCheckMapper;
//        this.appointmentsRepository = appointmentsRepository;
//        this.serviceInformationRepository = serviceInformationRepository;
//        this.userRepository = userRepository;
        this.userService = userService;
        this.userRepository = userRepository;

        this.answerCheckMapper = answerCheckMapper;
        this.objectMapper = objectMapper;
    }

    public void test11() {
        User currentUser = new User();
        currentUser.setChatId("111");
//        Appointment appointment = new Appointment();
//        ServiceInformation serviceInformation = new ServiceInformation();
//        appointment.setServicesInformation(List.of(serviceInformation));
//        currentUser.setAppointments(List.of(appointment));

        userRepository.save(currentUser);


        Scanner scanner = new Scanner(System.in);


        while (true) {
            String question = scanner.nextLine();
            String currentChatId = currentUser.getChatId();


            String answer = assistantTest.chat(question, "111");
            log.info("Ответ от тест чата {}", answer);
        }
    }

    public void testsTests() {
        String exampleJson = """
                {
                    "success": true,
                    "data": [
                        {
                            "time": "10:00",
                            "seance_length": 7200,
                            "sum_length": 7200,
                            "datetime": "2024-09-08T10:00:00+07:00"
                        },
                        {
                            "time": "14:00",
                            "seance_length": 7200,
                            "sum_length": 7200,
                            "datetime": "2024-09-08T14:00:00+07:00"
                        }
                    ],
                    "meta": []
                }
                """;


        try {
            FreeSessionForBookDTO request = objectMapper.readValue(exampleJson, FreeSessionForBookDTO.class);

            log.info(request.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}

