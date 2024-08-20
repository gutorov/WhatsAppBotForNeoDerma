package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.AppointmentsRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.YClientServiceImpl;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@Service
@Slf4j
public class TestService {
    @Value("${open.ai.token}")
    private String openAiToken;
    private final YClientServiceImpl yClientService;
    private final ServiceMapper serviceMapper;
    private final EmployeeMapper employeeMapper;
    private final AppointmentsRepository appointmentsRepository;
    private final ServiceInformationRepository serviceInformationRepository;
    private final UserRepository userRepository;

    public TestService(
            @Value("${open.ai.token}") String openAiToken,
            YClientServiceImpl yClientService,
            ServiceMapper serviceMapper,
            EmployeeMapper employeeMapper,
            AppointmentsRepository appointmentsRepository,
            ServiceInformationRepository serviceInformationRepository,
            UserRepository userRepository
    ) {
        this.openAiToken = openAiToken;
        this.yClientService = yClientService;
        this.serviceMapper = serviceMapper;
        this.employeeMapper = employeeMapper;
        this.appointmentsRepository = appointmentsRepository;
        this.serviceInformationRepository = serviceInformationRepository;
        this.userRepository = userRepository;
    }

    public void test11() {
        User currentUser = new User();
        currentUser.setChatId("111");
        Appointment appointment = new Appointment();
        ServiceInformation serviceInformation = new ServiceInformation();
        appointment.setServicesInformation(List.of(serviceInformation));
        currentUser.setAppointments(List.of(appointment));

        //тестовая модель
        ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
                .apiKey(openAiToken)
//                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
                .logRequests(true)
                .logRequests(true)
                .build();

        //тестовый клиент ассистента для работы с модлеью
        AssistantTest assistant = AiServices.builder(AssistantTest.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .tools(new Tools(
                        yClientService,
                        serviceMapper,
                        employeeMapper,
                        serviceInformationRepository,
                        appointmentsRepository,
                        userRepository,
                        currentUser,
                        appointment,
                        serviceInformation
                ))
                .build();


        Scanner scanner = new Scanner(System.in);


        while (true) {
            String question = scanner.nextLine();

            String answer = assistant.chat(question);
            log.info("Ответ от тест чата {}", answer);
        }
    }
}

