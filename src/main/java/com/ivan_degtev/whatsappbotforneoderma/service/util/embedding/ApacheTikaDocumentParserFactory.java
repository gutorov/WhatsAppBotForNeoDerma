package com.ivan_degtev.whatsappbotforneoderma.service.util.embedding;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.spi.data.document.parser.DocumentParserFactory;

public class ApacheTikaDocumentParserFactory implements DocumentParserFactory {

    @Override
    public DocumentParser create() {
        return new ApacheTikaDocumentParser();
    }
}
