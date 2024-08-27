package com.ivan_degtev.whatsappbotforneoderma.config;

import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.AIAnalyzer;
import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.Assistant;
import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.QuestionAnalyzer;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.AnswerCheckMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.NearestAvailableSessionMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.AppointmentsRepository;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient.YClientServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import com.ivan_degtev.whatsappbotforneoderma.tests.AssistantTest;
import com.ivan_degtev.whatsappbotforneoderma.tests.Tools;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


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

    private final JsonLoggingService jsonLogging;
    private final PersistentChatMemoryStore persistentChatMemoryStore;

    public AIConfig(
            YClientServiceImpl yClientService,
            ServiceMapper serviceMapper,
            EmployeeMapper employeeMapper,
            NearestAvailableSessionMapper nearestAvailableSessionMapper,
            AnswerCheckMapper answerCheckMapper,
            AppointmentsRepository appointmentsRepository,
            ServiceInformationRepository serviceInformationRepository,
            UserRepository userRepository,
            JsonLoggingService jsonLogging,
            PersistentChatMemoryStore persistentChatMemoryStore
    ) {
        this.yClientService = yClientService;
        this.serviceMapper = serviceMapper;
        this.employeeMapper = employeeMapper;
        this.nearestAvailableSessionMapper = nearestAvailableSessionMapper;
        this.answerCheckMapper = answerCheckMapper;
        this.appointmentsRepository = appointmentsRepository;
        this.serviceInformationRepository = serviceInformationRepository;
        this.userRepository = userRepository;
        this.jsonLogging = jsonLogging;
        this.persistentChatMemoryStore = persistentChatMemoryStore;
    }

    @Bean
    public AIAnalyzer aiServices() {
        return AiServices.builder(AIAnalyzer.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(100))
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
//                .temperature(0.5)
                .logRequests(true)
                .logRequests(true)
                .timeout(Duration.ofSeconds(30))
                .build();
    }



    //тест
    @Bean
    public AssistantTest assistantTest() {
        /*
          Создание объекта постоянной памяти на основе компонента - внутреннего хранилица памяти PersistentChatMemoryStore
         */
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(100)
                .chatMemoryStore(persistentChatMemoryStore)
                .build();

        return AiServices.builder(AssistantTest.class)
                .chatLanguageModel(chatLanguageModel())
//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .chatMemoryProvider(chatMemoryProvider)
                .tools(new Tools(
                        yClientService,
                        serviceMapper,
                        employeeMapper,
                        nearestAvailableSessionMapper,
                        answerCheckMapper,
                        serviceInformationRepository,
                        appointmentsRepository,
                        userRepository,
                        jsonLogging
                ))
                .build();
    }
    @Bean
    public QuestionAnalyzer QuestionAnalyzer() {
        return AiServices.builder(QuestionAnalyzer.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(1))
                .build();
    }
}
