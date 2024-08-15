package com.ivan_degtev.whatsappbotforneoderma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SendingMessageResponse {

    private Delivery delivery;
    private Meta meta;

    @Getter
    @Setter
    @ToString
    public static class Delivery {
        @JsonProperty("callback_url")
        private String callbackUrl;
        @JsonProperty("dispatch_routing")
        private String[] dispatchRouting;
        @JsonProperty("external_id")
        private String externalId;
        private Long id;
        private String phone;
        private String priority;
        @JsonProperty("scheduled_at")
        private String scheduledAt;
        @JsonProperty("sender_name")
        private String senderName;
        @JsonProperty("status_description")
        private String statusDescription;
        @JsonProperty("status_id")
        private int statusId;
        private String sum;
        @JsonProperty("total_sms")
        private int totalSms;
        @JsonProperty("traffic_category")
        private int trafficCategory;
        @JsonProperty("utm_mark")
        private String utmMark;
        @JsonProperty("reply_to_message_id")
        private String replyToMessageId;
        @JsonProperty("simulate_typing")
        private boolean simulateTyping;
    }

    @Getter
    @Setter
    @ToString
    public static class Meta {
        private int code;
        private String status;
    }
}
