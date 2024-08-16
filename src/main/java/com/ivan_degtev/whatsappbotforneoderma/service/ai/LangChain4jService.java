package com.ivan_degtev.whatsappbotforneoderma.service.ai;

import com.ivan_degtev.whatsappbotforneoderma.component.DailyScheduler;
import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.AIAnalyzer;
import com.ivan_degtev.whatsappbotforneoderma.config.interfaces.Assistant;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.DataForWhiteDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.ServiceDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@Service
@Slf4j
@AllArgsConstructor
public class LangChain4jService {
    private Assistant assistant;
    private AIAnalyzer aiAnalyzer;
    private DailyScheduler dailyScheduler;

    public void LangChain4jMainModule() {
        log.info("LangChain4jMainModule started");


        Scanner scanner = new Scanner(System.in);
        String question;
        DataForWhiteDTO dataForWhiteDTO;

        while (true) {
            System.out.print("Введите вопрос (или 'exit' для завершения): ");
            question = scanner.nextLine();

            if (question.equalsIgnoreCase("exit")) {
                log.info("Завершение программы.");
                break;
            }
            // логика работы с уровнем диалога
            String analyzedAnswer = aiAnalyzer.chat(question); //получить число
            if (analyzedAnswer.equals("1")) {
                String assistantAnswer = assistant.greeting(question);
                log.info("Ответ: " + assistantAnswer);
            } else if (analyzedAnswer.equals("2")) {
                List<ServiceDTO> serviceDTOList = dailyScheduler.getServicesDTOList();
                String assistantAnswer = assistant.onlyService(serviceDTOList, question);
                log.info("Ответ: " + assistantAnswer);
            }
//            String answer = assistant.chat(question);
//            log.info("Ответ: " + answer);
        }
        scanner.close();
    }
}
