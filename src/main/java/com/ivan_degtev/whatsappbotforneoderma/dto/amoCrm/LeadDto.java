package com.ivan_degtev.whatsappbotforneoderma.dto.amoCrm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadDto {
    private String name;
    private Integer price;

    @JsonProperty("status_id")
    private Integer statusId;

    @JsonProperty("pipeline_id")
    private Integer pipelineId;

    /**
     * Передавать всегда 0 - значит создано роботом(по спец. амосрм)
     */
    @JsonProperty("created_by")
    private Integer createdBy;

    @JsonProperty("custom_fields_values")
    private List<CustomFieldDto> customFieldsValues;
}
