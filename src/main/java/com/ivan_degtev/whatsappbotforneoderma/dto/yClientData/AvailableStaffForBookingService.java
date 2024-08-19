package com.ivan_degtev.whatsappbotforneoderma.dto.yClientData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AvailableStaffForBookingService {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("bookable")
    private boolean bookable;

    @JsonProperty("specialization")
    private String specialization;

    @JsonProperty("information")
    private String information;
}

