package com.ivan_degtev.whatsappbotforneoderma.dto.yClientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServiceDTO {
    private String id;
    private String title;
//    @JsonProperty("category_id")
//    private String categoryId;
//    @JsonProperty("price_min")
//    private String priceMin;
//    @JsonProperty("price_max")
//    private String priceMax;
}
