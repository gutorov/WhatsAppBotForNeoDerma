package com.ivan_degtev.whatsappbotforneoderma.config;

import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.AIAnalyzer;
import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.Assistant;
import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.QuestionAnalyzer;
import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.RAGAssistant;
import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
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

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import dev.langchain4j.data.document.Document;


import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;


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
//                .modelName("ft:gpt-3.5-turbo-0125:zorinov-ai:neo-derma-v-1:A1sqM4xE")
//                .modelName("ft:gpt-3.5-turbo-0125:zorinov-ai:neo-derma-v-1:A1yMrRYU")
                .logRequests(true)
                .logRequests(true)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(openAiToken)
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE)
                .build();
    }
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public RAGAssistant ragAssistant() {
        var contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore())
                .embeddingModel(embeddingModel())
                .maxResults(10) // Увеличиваем количество возвращаемых документов (по умолчанию часто 5)
//                .minScore(0.75) // Устанавливаем минимальный порог схожести для возвращаемых документов
                .build();

        var contentInjector = DefaultContentInjector.builder()
                .metadataKeysToInclude(asList("file_name", "index"))
                .build();

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(contentInjector)
                .build();

        return AiServices.builder(RAGAssistant.class)
                .chatLanguageModel(chatLanguageModel())
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemoryProvider(chatMemoryProvider())
                .build();
    }


//    @Bean
//    @Lazy
//    public AssistantTest assistantTest() {
//        /*
//        Создание объекта постоянной памяти на основе компонента - внутреннего хранилица памяти PersistentChatMemoryStore
//         */
//        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
//                .id(memoryId)
//                .maxMessages(100)
//                .chatMemoryStore(persistentChatMemoryStore)
//                .build();
//
//        String employees = yClientService.getListEmployeesAvailableForBooking(null, null).block();
//        String services = yClientService.getListServicesAvailableForBooking(null, null, null).block();
//        List<String> employeeList = Arrays.asList(Objects.requireNonNull(employees).split("\n"));  // Разбиваем строку по разделителю
//        List<String> serviceList = Arrays.asList(Objects.requireNonNull(services).split("\n"));
//
//        // Конвертируем данные в документы
//        List<Document> documents = employeeList.stream()
//                .map(e -> new Document(e.toString())) // Преобразуем каждого сотрудника в строку и затем в документ
//                .filter(doc -> doc.text() != null && !doc.text().trim().isEmpty()) // Фильтруем документы с непустым содержимым
//                .collect(Collectors.toList());
//
//        documents.addAll(serviceList.stream()
//                .map(s -> new Document(s.toString())) // Преобразуем каждую услугу в строку и затем в документ
//                .filter(doc -> doc.text() != null && !doc.text().trim().isEmpty()) // Фильтруем документы с непустым содержимым
//                .collect(Collectors.toList()));
//
//        // Разбиваем документы на сегменты
//        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
//        List<TextSegment> segments = documents.stream()
//                .flatMap(doc -> splitter.split(doc).stream())
//                .filter(segment -> segment.text() != null && !segment.text().trim().isEmpty()) // Фильтруем сегменты с непустым содержимым
//                .collect(Collectors.toList());
//
//        // Генерируем embeddings
//        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
//        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
//
//        // Создаем EmbeddingStore
//        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//        embeddingStore.addAll(embeddings, segments);
//
//        // Настраиваем контент-ретривер
//        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(embeddingStore)
//                .embeddingModel(embeddingModel)
//                .maxResults(2)
//                .minScore(0.5)
//                .build();
//
//        return AiServices.builder(AssistantTest.class)
//                .chatLanguageModel(chatLanguageModel())
//                .contentRetriever(contentRetriever)
//                .chatMemoryProvider(chatMemoryProvider)
////                .tools(new Tools(
////                        yClientService,
////                        serviceMapper,
////                        employeeMapper,
////                        nearestAvailableSessionMapper,
////                        answerCheckMapper,
////                        serviceInformationRepository,
////                        appointmentsRepository,
////                        userRepository,
////                        jsonLogging
////                ))
//                .build();
//
//    }
    //тест
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(100)
                .chatMemoryStore(persistentChatMemoryStore)
                .build();
    }
    @Bean
    public AssistantTest assistantTest() {
        /*
          Создание объекта постоянной памяти на основе компонента - внутреннего хранилица памяти PersistentChatMemoryStore
         */
//        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
//                .id(memoryId)
//                .maxMessages(100)
//                .chatMemoryStore(persistentChatMemoryStore)
//                .build();

        return AiServices.builder(AssistantTest.class)
                .chatLanguageModel(chatLanguageModel())
//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .chatMemoryProvider(chatMemoryProvider())
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
