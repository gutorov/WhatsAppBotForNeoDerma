package com.ivan_degtev.whatsappbotforneoderma.config;

import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.AIAnalyzer;
import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.Assistant;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.AnswerCheckMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.NearestAvailableSessionMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.AppointmentsRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.YClientServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.tests.AssistantTest;
import com.ivan_degtev.whatsappbotforneoderma.tests.Tools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AIConfig {

    @Value("${open.ai.token}")
    private String openAiToken;


    private final YClientServiceImpl yClientService;

    private final ServiceMapper serviceMapper;
    private final EmployeeMapper employeeMapper;
    private final NearestAvailableSessionMapper nearestAvailableSessionMapper;
    private final AnswerCheckMapper answerCheckMapper;
    private final AppointmentsRepository appointmentsRepository;
    private final ServiceInformationRepository serviceInformationRepository;
    private final UserRepository userRepository;


    public AIConfig(
            YClientServiceImpl yClientService,
            ServiceMapper serviceMapper,
            EmployeeMapper employeeMapper,
            NearestAvailableSessionMapper nearestAvailableSessionMapper,
            AnswerCheckMapper answerCheckMapper,
            AppointmentsRepository appointmentsRepository,
            ServiceInformationRepository serviceInformationRepository,
            UserRepository userRepository
            ) {
        this.yClientService = yClientService;
        this.serviceMapper = serviceMapper;
        this.employeeMapper = employeeMapper;
        this.nearestAvailableSessionMapper = nearestAvailableSessionMapper;
        this.answerCheckMapper = answerCheckMapper;
        this.appointmentsRepository = appointmentsRepository;
        this.serviceInformationRepository = serviceInformationRepository;
        this.userRepository = userRepository;
    }

    @Bean
    public AIAnalyzer aiServices() {
        return AiServices.builder(AIAnalyzer.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
    /**
     * Настрйока ассистента - интерфейса, через AiServices билдер клиента
     * для работы с яз. моделями через фреймворк langchain4j
     */
    @Bean
    public Assistant assistant() {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(1))
                .tools()
                .build();
    }

    /**
     * Настройка самой языковой модели
     * Для демонстрации - использовать апи-ключ "демо"(работае из РФ), по идее, должно работать с реальным ключом -
     * для этого исп. закоментированное значение и добавить ключ в пропертя или в переменные среды
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiToken)
                .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
//                .responseFormat("json_object")
                .logRequests(true)
                .logRequests(true)
                .build();
    }



    //тест
    @Bean
    public AssistantTest assistantTest() {
        return AiServices.builder(AssistantTest.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .tools(new Tools(
                        yClientService,
                        serviceMapper,
                        employeeMapper,
                        nearestAvailableSessionMapper,
                        answerCheckMapper,
                        serviceInformationRepository,
                        appointmentsRepository,
                        userRepository
                ))
                .build();
    }

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
}
