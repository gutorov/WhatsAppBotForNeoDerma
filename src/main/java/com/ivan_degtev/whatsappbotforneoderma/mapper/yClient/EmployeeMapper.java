package com.ivan_degtev.whatsappbotforneoderma.mapper.yClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeMapper {
    private ObjectMapper objectMapper;

    public List<EmployeeDTO> mapJsonToEmployeeList(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode dataNode = rootNode.path("data");
            log.info("В трай блоке имплои маппера, jsonNode dataNode {}", dataNode);

            return StreamSupport.stream(dataNode.spliterator(), false)
                    .map(this::mapJsonNodeToEmployeeDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to map JSON to EmployeeDTO list", e);
        }
    }

    private EmployeeDTO mapJsonNodeToEmployeeDTO(JsonNode node) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(node.path("id").asText());
        dto.setName(node.path("name").asText());
        dto.setSpecialization(node.path("specialization").asText());
        log.info("В приватном методе маппинга, получил дто {}", dto);
        return dto;
    }
}
