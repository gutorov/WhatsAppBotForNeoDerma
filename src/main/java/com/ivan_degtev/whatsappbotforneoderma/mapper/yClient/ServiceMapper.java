package com.ivan_degtev.whatsappbotforneoderma.mapper.yClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.ServiceDTO;
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

    public List<ServiceDTO> mapJsonToServiceList(String json) {
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

    private ServiceDTO mapJsonNodeToServiceDTO(JsonNode node) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(node.path("id").asText());
        dto.setTitle(node.path("title").asText());
//        dto.setCategoryId(node.path("category_id").asText());
//        dto.setPriceMin(node.path("price_min").asText());
//        dto.setPriceMax(node.path("price_max").asText());
        log.info("В приватном методе маппинга, получил ServiceDTO: {}", dto);
        return dto;
    }
}
