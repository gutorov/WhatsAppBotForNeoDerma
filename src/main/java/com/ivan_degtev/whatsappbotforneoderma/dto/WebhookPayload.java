package com.ivan_degtev.whatsappbotforneoderma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.whatsappbotforneoderma.model.enums.Direction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class WebhookPayload {
    private String type;
    private Payload payload;

    @Setter
    @Getter
    @ToString
    @JsonIgnoreProperties({"delivery_id"})
    public static class Payload {
        @JsonProperty("new_message")
        private NewMessage newMessage;
        @JsonProperty("customer_id") //previously there was a delivery_id but i got a problem with the fact the payload content has changed
        private Long deliveryId;
    }

    @Setter
    @Getter
    @ToString
    public static class NewMessage {
        private MessageDetails message;
        private Direction direction;
        @JsonProperty("sender_id")
        private String senderId;
        @JsonProperty("chat_id")
        private String chatId;
        @JsonProperty("sender_name")
        private String senderName;
        @JsonProperty("sender_phone_number")
        private String senderPhoneNumber;
    }

    @Setter
    @Getter
    @ToString
    public static class MessageDetails {
        private String id;
        private Long timestamp;
        private String type;
        private String text;
        @JsonProperty("reply_to_message_id")
        private String replyToMessageId;
    }
}
