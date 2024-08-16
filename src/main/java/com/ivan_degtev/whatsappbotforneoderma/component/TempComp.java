package com.ivan_degtev.whatsappbotforneoderma.component;

import com.ivan_degtev.whatsappbotforneoderma.service.ai.LangChain4jService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class TempComp implements ApplicationRunner {
    private final LangChain4jService langChain4jService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("TempComp started");
      langChain4jService.LangChain4jMainModule();
    }
}
