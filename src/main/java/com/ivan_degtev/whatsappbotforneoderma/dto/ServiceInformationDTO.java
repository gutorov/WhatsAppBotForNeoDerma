package com.ivan_degtev.whatsappbotforneoderma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServiceInformationDTO {
    @JsonProperty("service_id")
    private String serviceId;
    private String title;
    @JsonProperty("price_min")
    private String priceMin;
    @JsonProperty("price_max")
    private String priceMax;

}
