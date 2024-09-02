package com.ivan_degtev.whatsappbotforneoderma.config;

import com.ivan_degtev.whatsappbotforneoderma.config.LC4jAssistants.RAGAssistant;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Configuration
@Slf4j
public class RagConfig {

//    @Value("${open.ai.token}")
//    private String openAiToken;
//    private final ChatLanguageModel chatLanguageModel;
//    private final ChatMemoryProvider chatMemoryProvider;
//
//    public RagConfig(
//            @Value("${open.ai.token}") String openAiToken,
//            ChatLanguageModel chatLanguageModel,
//            ChatMemoryProvider chatMemoryProvider
//    ) {
//        this.openAiToken = openAiToken;
//        this.chatLanguageModel = chatLanguageModel;
//        this.chatMemoryProvider = chatMemoryProvider;
//    }
//
//    @Bean
//    public EmbeddingModel embeddingModel() {
//        return OpenAiEmbeddingModel.builder()
//                .apiKey(openAiToken)
//                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE)
//                .build();
//    }
//    @Bean
//    public EmbeddingStore<TextSegment> embeddingStore() {
//        return new InMemoryEmbeddingStore<>();
//    }
//
//    @Bean
//    public RAGAssistant ragAssistant() {
//        var contentRetriever = EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(embeddingStore())
//                .embeddingModel(embeddingModel())
//                .maxResults(15)
//                .build();
//
//        var contentInjector = DefaultContentInjector.builder()
//                .metadataKeysToInclude(asList("file_name", "index"))
//                .build();
//
//        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
//                .contentRetriever(contentRetriever)
//                .contentInjector(contentInjector)
//                .build();
//
//        return AiServices.builder(RAGAssistant.class)
//                .chatLanguageModel(chatLanguageModel())
//                .retrievalAugmentor(retrievalAugmentor)
//                .chatMemoryProvider(chatMemoryProvider())
//                .build();
//    }

}
