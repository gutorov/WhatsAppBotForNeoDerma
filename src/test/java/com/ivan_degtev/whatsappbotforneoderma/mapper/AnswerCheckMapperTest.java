package com.ivan_degtev.whatsappbotforneoderma.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.FreeSessionForBookDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.yClient.AnswerCheckMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AnswerCheckMapperTest {

    private AnswerCheckMapper answerCheckMapper;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        answerCheckMapper = new AnswerCheckMapper(objectMapper);
    }

    @Test
    void mapJsonToFreeSessionForBookDTO_shouldMapCorrectly() {
        String json = "{\n" +
                "    \"success\": true,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"time\": \"16:00\",\n" +
                "            \"seance_length\": 7200,\n" +
                "            \"datetime\": \"2024-09-03T16:00:00+07:00\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"meta\": [\"Test message\"]\n" +
                "}";

        FreeSessionForBookDTO result = answerCheckMapper.mapJsonToFreeSessionForBookDTO(json);

        assertNotNull(result);
        assertTrue(result.isSuccess());

        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        FreeSessionForBookDTO.DataInformation dataInfo = result.getData().get(0);
        assertEquals("16:00", dataInfo.getTime());
        assertEquals(7200, dataInfo.getSeanceLength());
        assertEquals(OffsetDateTime.parse("2024-09-03T16:00:00+07:00"), dataInfo.getDateTime());

        assertNotNull(result.getMeta());
        assertEquals("Test message", result.getMeta().getMessage());
    }

    @Test
    void mapJsonToFreeSessionForBookDTO_shouldHandleEmptyMeta() {
        String json = "{\n" +
                "    \"success\": true,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"time\": \"16:00\",\n" +
                "            \"seance_length\": 7200,\n" +
                "            \"datetime\": \"2024-09-03T16:00:00+07:00\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"meta\": []\n" +
                "}";

        FreeSessionForBookDTO result = answerCheckMapper.mapJsonToFreeSessionForBookDTO(json);

        assertNotNull(result);
        assertTrue(result.isSuccess());

        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        FreeSessionForBookDTO.DataInformation dataInfo = result.getData().get(0);
        assertEquals("16:00", dataInfo.getTime());
        assertEquals(7200, dataInfo.getSeanceLength());
        assertEquals(OffsetDateTime.parse("2024-09-03T16:00:00+07:00"), dataInfo.getDateTime());

        assertNotNull(result.getMeta());
        assertNull(result.getMeta().getMessage());
    }
}

