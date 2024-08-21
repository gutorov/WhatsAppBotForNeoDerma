package com.ivan_degtev.whatsappbotforneoderma.dto.yClientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class FreeSessionForBookDTO {
    private boolean success;
    @JsonProperty("data")
    private List<DataInformation> data;
    @JsonProperty("meta")
    private MetaInformation meta;

    @Setter
    @Getter
    @ToString
    public static class DataInformation {

        private String time;
        @JsonProperty("seance_length")
        private Integer seanceLength;
        @JsonProperty("sum_length")
        private Integer sumLength;
        @JsonProperty("date_time")
        private OffsetDateTime dateTime;

    }
    @Setter
    @Getter
    @ToString
    public static class MetaInformation {
        String message;
    }
}
