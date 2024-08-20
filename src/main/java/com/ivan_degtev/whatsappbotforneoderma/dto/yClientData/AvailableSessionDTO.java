package com.ivan_degtev.whatsappbotforneoderma.dto.yClientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Setter
@Getter
@ToString
public class AvailableSessionDTO {
    @JsonProperty("time")
    private String time;
    @JsonProperty("seance_length")
    private Integer seanceLength;
    @JsonProperty("date_time")
    private String dateTime;
//    private OffsetDateTime dateTime;
}
