package com.ivan_degtev.whatsappbotforneoderma.component;

import com.ivan_degtev.whatsappbotforneoderma.service.ai.LangChain4jService;
import com.ivan_degtev.whatsappbotforneoderma.tests.TestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final LangChain4jService langChain4jService;
    private final TestService testService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("DataInitializer started");

        //Раскомментировать для теста ЛЛМ локально через сканнер
        langChain4jService.testLLMLogicWithScanner();

//        langChain4jService.testAmo();
//        testService.testsTests();
//        langChain4jService.testSendMessage();
    }
}
