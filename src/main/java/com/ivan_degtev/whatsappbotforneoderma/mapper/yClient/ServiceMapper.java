package com.ivan_degtev.whatsappbotforneoderma.mapper.yClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import com.ivan_degtev.whatsappbotforneoderma.repository.yClient.ServiceInformationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class ServiceMapper {
    private final ObjectMapper objectMapper;
    private final ServiceInformationRepository serviceInformationRepository;

    public List<ServiceInformation> mapJsonToServiceList(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode servicesNode = rootNode.path("data").path("services");

            return StreamSupport.stream(servicesNode.spliterator(), false)
                    .map(this::mapJsonNodeToServiceDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to map JSON to ServiceDTO list", e);
        }
    }

    /**
     * Первично при запуске парсим все сервисы и сохраняем в локал БД их базовую ирнформацию
     * - внешний айди и название
     */
    private ServiceInformation mapJsonNodeToServiceDTO(JsonNode node) {
        ServiceInformation serviceInformation = new ServiceInformation();
        serviceInformation.setServiceId(node.path("id").asText());
        serviceInformation.setTitle(node.path("title").asText());

        serviceInformationRepository.save(serviceInformation);
        return serviceInformation;
    }
}
