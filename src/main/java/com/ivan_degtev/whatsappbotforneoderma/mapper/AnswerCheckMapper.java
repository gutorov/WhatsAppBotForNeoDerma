package com.ivan_degtev.whatsappbotforneoderma.mapper;

import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.FreeSessionForBookDTO;
import com.ivan_degtev.whatsappbotforneoderma.mapper.config.JsonNullableMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.config.ReferenceMapper;
import org.mapstruct.*;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class AnswerCheckMapper {

//    @Mapping(source = "response.success", target = "success")
//    @Mapping(source = "response.data.time", target = "data.time")
//    @Mapping(source = "response.data.seance_length", target = "data.seanceLength")
//    @Mapping(source = "response.data.sum_length", target = "data.sumLength")
//    @Mapping(source = "response.datetime", target = "data.dateTime")
//    @Mapping(source = "meta.message", target = "meta.message")
//    public abstract FreeSessionForBookDTO convertResponseToFreeSessionDTO(String response);
}