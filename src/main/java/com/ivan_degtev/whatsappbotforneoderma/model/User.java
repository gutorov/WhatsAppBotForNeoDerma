package com.ivan_degtev.whatsappbotforneoderma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.whatsappbotforneoderma.model.interfaces.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ключевое поле - неизменно при запросах и ответах, поиск по нему в БД
    @NotNull
    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("sender_name")
    private String senderName;

    @JsonProperty("sender_phone_number")
    private String senderPhoneNumber;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

}
