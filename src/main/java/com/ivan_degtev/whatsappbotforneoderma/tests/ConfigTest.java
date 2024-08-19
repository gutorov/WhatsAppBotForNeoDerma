package com.ivan_degtev.whatsappbotforneoderma.tests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigTest {

    @Value("${open.ai.token}")
    private String openAiToken;

}
