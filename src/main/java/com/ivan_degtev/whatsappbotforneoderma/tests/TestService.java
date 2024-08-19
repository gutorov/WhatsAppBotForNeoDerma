package com.ivan_degtev.whatsappbotforneoderma.tests;

import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Scanner;

@Service
@Slf4j
@AllArgsConstructor
public class TestService {
    private final ConfigTest configTest;
    private final         AssistantTest assistantTest;


    public void test11() {

        Scanner scanner = new Scanner(System.in);


        while(true) {
            ServiceInformation serviceInformation = new ServiceInformation();
            String question = scanner.nextLine();

            String answer = assistantTest.chat(serviceInformation, question);
            log.info("Ответ от тест чата {}", answer);
            log.info("Чат также изменил объект serviceInformation {}", serviceInformation);
        }
    }
}
