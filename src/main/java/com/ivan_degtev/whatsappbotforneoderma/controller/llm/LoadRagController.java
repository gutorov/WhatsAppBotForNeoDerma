package com.ivan_degtev.whatsappbotforneoderma.controller.llm;

import com.ivan_degtev.whatsappbotforneoderma.component.EmbeddingComponent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class LoadRagController {

    private final EmbeddingComponent embeddingComponent;

    @GetMapping("/load")
    public void loadSingle() {
        log.info("Loading company documents start");
        embeddingComponent.loadCompanyServiceJsonFromYclient();
        log.info("Loading company documents finish");

        log.info("Загружаем файлы из директории для обучения");
        embeddingComponent.findAndLoadAllDocuments();
        log.info("успешно загружены файлы из директории для обучения");
    }

}
