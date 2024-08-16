package com.ivan_degtev.whatsappbotforneoderma.dto.yClientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ДТО со всеми первичными данными о записи, нужными для записи на прием(не хватает id записи, пока хз, где ее брать
 * в доке пишут, что автмоатически появится, неясно где и как...)
 */
@Getter
@Setter
@ToString
public class DataForWhiteDTO {
    private String phone;
    @JsonProperty("full_name")
    private String fullName;
    private List<Appointments> appointments;

    @Setter
    @Getter
    @ToString
    public static class Appointments {
        @JsonProperty("service_id")
        private String[] serviceId;
        @JsonProperty("staff_id")
        private String staffId;
        private LocalDateTime datetime;
    }

}
