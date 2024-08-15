package com.ivan_degtev.whatsappbotforneoderma.mapper;

import com.ivan_degtev.whatsappbotforneoderma.dto.WebhookPayload;
import com.ivan_degtev.whatsappbotforneoderma.mapper.config.JsonNullableMapper;
import com.ivan_degtev.whatsappbotforneoderma.mapper.config.ReferenceMapper;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import org.mapstruct.*;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Mapping(source = "payload.newMessage.chatId.", target = "chatId")
    @Mapping(source = "payload.newMessage.senderName.", target = "senderName")
    @Mapping(source = "payload.newMessage.senderPhoneNumber.", target = "senderPhoneNumber")
    public abstract User convertWebhookPayloadToUser(WebhookPayload webhookPayload);
}
