package com.ivan_degtev.whatsappbotforneoderma.dto.yClientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


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
}
