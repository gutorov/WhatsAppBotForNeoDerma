package com.ivan_degtev.whatsappbotforneoderma.mapper.yClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.ServiceInformationDTO;
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

    public List<ServiceInformationDTO> mapJsonToServiceList(String json) {
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
     * Первично при запуске парсим все сервисы и сохраняем  их базовую ирнформаци - внешний айди и название в дто
     */
    private ServiceInformationDTO mapJsonNodeToServiceDTO(JsonNode node) {
        ServiceInformationDTO serviceInformationDTO = new ServiceInformationDTO();
        serviceInformationDTO.setServiceId(node.path("id").asText());
        serviceInformationDTO.setTitle(node.path("title").asText());
        serviceInformationDTO.setPriceMin(node.path("price_min").asText());
        serviceInformationDTO.setPriceMax(node.path("price_max").asText());

        return serviceInformationDTO;
    }
}
