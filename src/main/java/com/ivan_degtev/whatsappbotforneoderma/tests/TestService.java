package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.model.User;

import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;

import com.ivan_degtev.whatsappbotforneoderma.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@Slf4j
public class TestService {
    private final UserService userService;
    private final UserRepository userRepository;
    @Value("${open.ai.token}")
    private String openAiToken;
    private final AssistantTest assistantTest;

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
            UserRepository userRepository
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
    }

    public void test11() {
        User currentUser = new User();
        currentUser.setChatId("111");
//        Appointment appointment = new Appointment();
//        ServiceInformation serviceInformation = new ServiceInformation();
//        appointment.setServicesInformation(List.of(serviceInformation));
//        currentUser.setAppointments(List.of(appointment));

        userRepository.save(currentUser);

        //тестовая модель
//        ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
//                .apiKey(openAiToken)
//                .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
//                .logRequests(true)
//                .logRequests(true)
//                .build();
//
//        //тестовый клиент ассистента для работы с модлеью
//        AssistantTest assistant = AiServices.builder(AssistantTest.class)
//                .chatLanguageModel(chatLanguageModel)
//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
//                .tools(new Tools(
//                        yClientService,
//                        serviceMapper,
//                        employeeMapper,
//                        availableSessionMapper,
//                        answerCheckMapper,
//                        serviceInformationRepository,
//                        appointmentsRepository,
//                        userRepository,
//                        currentUser,
//                        appointment,
//                        serviceInformation
//                ))
//                .build();


        Scanner scanner = new Scanner(System.in);


        while (true) {
            String question = scanner.nextLine();
            String currentChatId = currentUser.getChatId();


            String answer = assistantTest.chat(question, "111");
            log.info("Ответ от тест чата {}", answer);
        }
    }
}

