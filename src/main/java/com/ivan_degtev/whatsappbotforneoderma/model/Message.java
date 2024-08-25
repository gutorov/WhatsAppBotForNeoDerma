package com.ivan_degtev.whatsappbotforneoderma.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ivan_degtev.whatsappbotforneoderma.model.enums.Direction;
import com.ivan_degtev.whatsappbotforneoderma.model.interfaces.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Message implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @JsonProperty("chat_push_message_id")
    private String chatPushMessageId;
    private String type;
    private String text;

    @JsonProperty("data_time")
    private LocalDateTime dataTime;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private User user;

}
