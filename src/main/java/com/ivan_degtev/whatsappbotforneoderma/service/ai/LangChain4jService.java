package com.ivan_degtev.whatsappbotforneoderma.service.ai;

import com.ivan_degtev.whatsappbotforneoderma.controller.WhatsAppSendController;
import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import com.ivan_degtev.whatsappbotforneoderma.tests.AssistantTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.UUID;

@Service
@Slf4j
@DependsOn("dailyScheduler")
public class LangChain4jService {
    private final JsonLoggingService jsonLoggingService;
//    private Assistant assistant;
//    private AIAnalyzer aiAnalyzer;
//    private DailyScheduler dailyScheduler;
//    private final YClientController yClientController;
//    private final EmployeeMapper employeeMapper;
//    private final ServiceInformationRepository serviceInformationRepository;
//    private final AppointmentsRepository appointmentsRepository;

    @Value("${open.ai.token}")
    private String openAiToken;
    private final AssistantTest assistantTest;
    private final UserRepository userRepository;
//    private final ChatPushService chatPushService;
    private final WhatsAppSendController whatsAppSendController;
    private final JsonLoggingService jsonLogging;

    public LangChain4jService(
            @Value("${open.ai.token}") String openAiToken,
            AssistantTest assistantTest,
            UserRepository userRepository,
//            @Qualifier("chatPushSenderServiceImpl") ChatPushService chatPushService
            WhatsAppSendController whatsAppSendController,
            JsonLoggingService jsonLogging,
            JsonLoggingService jsonLoggingService) {
        this.openAiToken = openAiToken;
        this.assistantTest = assistantTest;
        this.userRepository = userRepository;
//        this.chatPushService = chatPushService;
        this.whatsAppSendController = whatsAppSendController;
        this.jsonLogging = jsonLogging;
        this.jsonLoggingService = jsonLoggingService;
    }


    public void mainMethodByWorkWithLLM(
            User currentUser,
            Message currentMessage
    ) {
        String currentChatId = currentUser.getChatId();
        String textMessage = currentMessage.getText();
        String currentUserPhone = currentUser.getSenderPhoneNumber();

        String LLMAnswer = assistantTest.chat(currentChatId, textMessage, currentChatId);
        jsonLogging.info("LLM answer: {}", LLMAnswer);


        var answerFromSendMessage = whatsAppSendController.sendMessage(LLMAnswer, currentUserPhone).subscribe();
        jsonLogging.info("Отправил сообщение из сервиса LangChain4j в чатпуш сервис - в сообщению юзеру, " +
                "ответ от метода отправки {}", answerFromSendMessage);
    }


    /**
     * Тестовый формат метода для быстрой проверки ЛЛМ через сканер.
     * Подключить в DataInitializer(удобнее) или вызвать вручную из кода
     */
    public void testLLMLogicWithScanner() {
        User currentUser = new User();
        currentUser.setChatId("111");
        currentUser.setUniqueIdForAppointment(UUID.randomUUID().toString());
        userRepository.save(currentUser);


        Scanner scanner = new Scanner(System.in);
        while (true) {
            String question = scanner.nextLine();

            if (question.equals("exit")) {
                log.info("тестовый сканер закрыт!");
                break;
            }
            String currentChatId = currentUser.getChatId();

            String answer = assistantTest.chat(currentChatId, question, currentChatId);
            log.info("Ответ от тест чата, сканер: {}", answer);
        }
        log.info("Сканнер закрыт!");
    }

    public void testSendMessage() {
        Scanner scanner = new Scanner(System.in);
        String currentUserPhone = "79951489346";

        while (true) {
            String question = scanner.nextLine();

            if (question.equals("exit")) {
                log.info("тестовый сканер закрыт!");
                break;
            }

            var answerFromSendMessage = whatsAppSendController.sendMessage(question, currentUserPhone).block();
            jsonLogging.info("Отправил сообщение из тестового метода в чатпуш сервис - в сообщению юзеру, " +
                    "ответ от метода отправки {}", answerFromSendMessage);
        }

    }


}

