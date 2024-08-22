package com.ivan_degtev.whatsappbotforneoderma.service.ai;

import com.ivan_degtev.whatsappbotforneoderma.controller.WhatsAppController;
import com.ivan_degtev.whatsappbotforneoderma.controller.WhatsAppSendController;
import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import com.ivan_degtev.whatsappbotforneoderma.service.ChatPushService;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.ChatpushServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.UserService;
import com.ivan_degtev.whatsappbotforneoderma.tests.AssistantTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UrlPathHelper;

import java.util.Scanner;

@Service
@Slf4j
@DependsOn("dailyScheduler")
public class LangChain4jService {
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

    public LangChain4jService(
            @Value("${open.ai.token}") String openAiToken,
            AssistantTest assistantTest,
            UserRepository userRepository,
//            @Qualifier("chatPushSenderServiceImpl") ChatPushService chatPushService
            WhatsAppSendController whatsAppSendController
    ) {
        this.openAiToken = openAiToken;
        this.assistantTest = assistantTest;
        this.userRepository = userRepository;
//        this.chatPushService = chatPushService;
        this.whatsAppSendController = whatsAppSendController;
    }


    public void mainMethodByWorkWithLLM(
            User currentUser,
            Message currentMessage
    ) {
        String currentChatId = currentUser.getChatId();
        String textMessage = currentMessage.getText();
        String currentUserPhone = currentUser.getSenderPhoneNumber();

        String LLMAnswer = assistantTest.chat(textMessage, currentChatId);
        log.info("LLM answer: {}", LLMAnswer);


        var answerFromSendMessage = whatsAppSendController.sendMessage(LLMAnswer, currentUserPhone);
        log.info("Отправил сообщение из сервиса LangChain4j в чатпуш сервис - в сообщению юзеру, " +
                "ответ от метода отправки {}", answerFromSendMessage);
    }


    /**
     * Тестовый формат метода для быстрой проверки ЛЛМ через сканер.
     * Подключить в DataInitializer(удобнее) или вызвать вручную из кода
     */
    public void testLLMLogicWithScanner() {
        User currentUser = new User();
        currentUser.setChatId("111");

        userRepository.save(currentUser);


        Scanner scanner = new Scanner(System.in);
        while (true) {
            String question = scanner.nextLine();

            if (question.equals("exit")) {
                log.info("тестовый сканер закрыт!");
                break;
            }
            String currentChatId = currentUser.getChatId();

            String answer = assistantTest.chat(question, "111");
            log.info("Ответ от тест чата {}", answer);
        }
    }
}

