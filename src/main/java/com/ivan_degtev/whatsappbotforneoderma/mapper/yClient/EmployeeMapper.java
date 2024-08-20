package com.ivan_degtev.whatsappbotforneoderma.mapper.yClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.AvailableStaffForBookingService;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeMapper {
    private ObjectMapper objectMapper;

    /**
     * Метод мапит json в сущность в данными о сотрудниках,
     * которые могут оказать указанную при запросе услугу в указанное дату/время
     */
    public List<AvailableStaffForBookingService> mapJsonToAvailableStaffForBookingService(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<AvailableStaffForBookingService> availableStaffForBookingServiceList = objectMapper.readValue(
                    objectMapper.readTree(json).get("data").toString(),
                    new TypeReference<List<AvailableStaffForBookingService>>() {}
            );
            log.info("замапил строку с сотрудниками, коих можно забронить на услугу + дату {}",
                    availableStaffForBookingServiceList);
            return availableStaffForBookingServiceList;

        } catch (IOException e) {
            throw new RuntimeException("Failed to map JSON to availableStaffForBookingService list", e);
        }
    }

    /**
     * Мапит общую инфу по сотрудникам
     */
    public List<EmployeeDTO> mapJsonToEmployeeList(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode dataNode = rootNode.path("data");

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
        return dto;
    }
}
