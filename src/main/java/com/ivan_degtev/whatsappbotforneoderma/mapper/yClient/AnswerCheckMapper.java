package com.ivan_degtev.whatsappbotforneoderma.mapper.yClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.AvailableSessionDTO;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.FreeSessionForBookDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Slf4j
@Service
public class AnswerCheckMapper {
    private final ObjectMapper objectMapper;

    public FreeSessionForBookDTO mapJsonToFreeSessionForBookDTO(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);

            FreeSessionForBookDTO freeSessionForBookDTO = new FreeSessionForBookDTO();
            freeSessionForBookDTO.setSuccess(rootNode.path("success").asBoolean());

            List<FreeSessionForBookDTO.DataInformation> dataList = new ArrayList<>();
            JsonNode dataArrayNode = rootNode.path("data");

            if (dataArrayNode.isArray()) {
                for (JsonNode dataNode : dataArrayNode) {
                    FreeSessionForBookDTO.DataInformation dataInfo = new FreeSessionForBookDTO.DataInformation();
                    dataInfo.setTime(dataNode.path("time").asText());
                    dataInfo.setSeanceLength(dataNode.path("seance_length").asInt());
                    dataInfo.setDateTime(OffsetDateTime.ofInstant(
                            Instant.ofEpochSecond(dataNode.path("datetime").asLong()), ZoneOffset.UTC));

                    dataList.add(dataInfo);
                }
            }

            freeSessionForBookDTO.setData(dataList);

            FreeSessionForBookDTO.MetaInformation metaInfo = new FreeSessionForBookDTO.MetaInformation();
            JsonNode metaNode = rootNode.path("meta");

            if (metaNode.isArray() && !metaNode.isEmpty()) {
                metaInfo.setMessage(metaNode.get(0).asText());
            }

            freeSessionForBookDTO.setMeta(metaInfo);

            return freeSessionForBookDTO;

        } catch (Exception e) {
            log.error("Failed to map JSON to FreeSessionForBookDTO", e);
            throw new RuntimeException("Failed to map JSON to FreeSessionForBookDTO", e);
        }
    }
}

