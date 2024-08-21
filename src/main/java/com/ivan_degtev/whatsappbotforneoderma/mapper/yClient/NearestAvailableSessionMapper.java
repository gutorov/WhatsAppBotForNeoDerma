package com.ivan_degtev.whatsappbotforneoderma.mapper.yClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.AvailableSessionDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@AllArgsConstructor
public class NearestAvailableSessionMapper {
    private final ObjectMapper objectMapper;

    public List<AvailableSessionDTO> mapJsonToNearestAvailableSessionList(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
//            JsonNode servicesNode = rootNode.path("data").path("seances");
//            JsonNode servicesNode = rootNode.path("data");
            JsonNode seancesNode = rootNode.path("data").path("seances");

            return StreamSupport.stream(seancesNode.spliterator(), false)
                    .map(this::mapJsonNodeToAvailableSessionDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to map JSON to AvailableSessionDTO list", e);
        }
    }
    public List<AvailableSessionDTO> mapJsonToAvailableSessionListForSpecificDate(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
//            JsonNode servicesNode = rootNode.path("data").path("seances");
            JsonNode servicesNode = rootNode.path("data");

            return StreamSupport.stream(servicesNode.spliterator(), false)
                    .map(this::mapJsonNodeToAvailableSessionDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to map JSON to AvailableSessionDTO list", e);
        }
    }

    private AvailableSessionDTO mapJsonNodeToAvailableSessionDTO(JsonNode node) {
        AvailableSessionDTO availableSessionDTO = new AvailableSessionDTO();
        availableSessionDTO.setTime(node.path("time").asText());
        availableSessionDTO.setSeanceLength(node.path("seance_length").asInt());
        availableSessionDTO.setDateTime(node.path("datetime").asText());
        log.info("Замапил 1 сущность свободного времени для записи на опред. дату к сотруднику {}",
                availableSessionDTO.toString());
        return availableSessionDTO;
    }
}
