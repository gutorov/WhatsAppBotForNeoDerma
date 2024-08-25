package com.ivan_degtev.whatsappbotforneoderma.mapper;

import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.mapper.config.JsonNullableMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.config.ReferenceMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.Message;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class MessageMapper {

    @Mapping(source = "payload.newMessage.direction", target = "direction")
    @Mapping(source = "payload.newMessage.message.timestamp", target = "dataTime")
    @Mapping(source = "payload.newMessage.message.text", target = "text")
    @Mapping(source = "payload.newMessage.message.type", target = "type")
    @Mapping(source = "payload.newMessage.message.id", target = "chatPushMessageId")
    public abstract Message convertWebhookPayloadToMessage(WebhookPayload webhookPayload);

    /**
     * Метод для преобразования Unix Timestamp в LocalDateTime.
     */
    protected LocalDateTime map(Long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
    }
}
