package com.ivan_degtev.whatsappbotforneoderma.service.ai;

import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.Assistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@Slf4j
@AllArgsConstructor
public class LangChain4jService {
    private Assistant assistant;

    public void LangChain4jMainModule() {
        log.info("LangChain4jMainModule started");

        Scanner scanner = new Scanner(System.in);
        String question;

        while (true) {
            System.out.print("Введите вопрос (или 'exit' для завершения): ");
            question = scanner.nextLine();

            if (question.equalsIgnoreCase("exit")) {
                log.info("Завершение программы.");
                break;
            }
            String answer = assistant.chat(question);
            log.info("Ответ: " + answer);
        }
        scanner.close();
    }
}
