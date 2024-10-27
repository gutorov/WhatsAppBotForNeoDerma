package com.ivan_degtev.whatsappbotforneoderma.component;

import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.ServiceMapper;
import com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient.YClientServiceImpl;
import com.ivan_degtev.whatsappbotforneoderma.service.util.embedding.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
@Slf4j
public class EmbeddingComponent {

    private final YClientServiceImpl yClientService;
    private final ServiceMapper serviceMapper;

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore embeddingStore;
    private final EmbeddingStoreIngestor embeddingStoreIngestor;

    private static final String FILES_DIRECTORY = "src/main/resources/files";

    //in docker volume
//    private static final String FILES_DIRECTORY = "/app/files";

    public void loadCompanyServiceJsonFromYclient() {
        String services = yClientService.getListServicesAvailableForBooking(null, null, null).block();

        List<ServiceInformationDTO> serviceDTOList = serviceMapper.mapJsonToServiceList(services);

        List<Document> documentsByServices = serviceDTOList
                .stream()
                .map(serv -> new Document(serv.toString()))
                .collect(Collectors.toList());


//        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
//                .documentSplitter(DocumentSplitters.recursive(300, 10))
//                .embeddingModel(embeddingModel)
//                .embeddingStore(embeddingStore)
//                .build();

        documentsByServices.forEach(embeddingStoreIngestor::ingest);
    }

    public void loadCompanyDocuments(String fileName) {
        DocumentParser parser = new ApacheTikaDocumentParser();

        String filePath = FILES_DIRECTORY + "/" + fileName;
        log.info("Полный путь к загруженному файлу для обучения векторной БД {}", filePath);

        try (InputStream inputStream = new FileInputStream(new File(filePath))) {
            log.info("Преобразовал файл по его пути {} в инпут стрим", filePath);

            Document document = parser.parse(inputStream);
            log.info("Запарсил файл с документ из lc4j {}", document.toString());

            embeddingStoreIngestor.ingest(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findAndLoadAllDocuments() {
        Path dirPath = Paths.get(FILES_DIRECTORY);

        try (Stream<Path> filePathStream = Files.list(dirPath)) {
            List<String> filesName = filePathStream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            for (String fileName : filesName) {
                loadCompanyDocuments(fileName);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файлов из директории: " + e.getMessage());
        }
    }
}
