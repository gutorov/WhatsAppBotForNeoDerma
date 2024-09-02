package com.ivan_degtev.whatsappbotforneoderma.dto.amoCrm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldDto {
    @JsonProperty("field_id")
    private Integer fieldId;

    private List<ValueDto> values;
}
