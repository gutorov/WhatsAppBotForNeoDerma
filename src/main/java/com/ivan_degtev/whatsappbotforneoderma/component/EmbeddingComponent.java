package com.ivan_degtev.whatsappbotforneoderma.component;

import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.ServiceDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.EmployeeMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient.YClientServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.util.JsonLoggingService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class EmbeddingComponent {

    private final YClientServiceImpl yClientService;
    private final EmployeeMapper employeeMapper;
    private final ServiceMapper serviceMapper;

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore embeddingStore;

    private final JsonLoggingService jsonLogging;

    public void loadCompanyDocuments() {
        String services = yClientService.getListServicesAvailableForBooking(null, null, null).block();

        List<ServiceInformationDTO> serviceDTOList = serviceMapper.mapJsonToServiceList(services);

        List<Document> documentsByServices = serviceDTOList
                .stream()
                .map(serv -> new Document(serv.toString()))
                .collect(Collectors.toList());


        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 10))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

//        documentsByEmployee.forEach(embeddingStoreIngestor::ingest);
        documentsByServices.forEach(embeddingStoreIngestor::ingest);

    }
}
