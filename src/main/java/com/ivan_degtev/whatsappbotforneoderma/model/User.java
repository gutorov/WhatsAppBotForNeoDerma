package com.ivan_degtev.whatsappbotforneoderma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.whatsappbotforneoderma.model.interfaces.BaseEntity;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString(exclude = { "messages", "appoitments" })
public class User implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ключевое поле - неизменно при запросах и ответах, поиск по нему в БД
    @Column(unique = true, nullable = false)
    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("sender_name")
    private String senderName;

    @JsonProperty("sender_phone_number")
    private String senderPhoneNumber;

    /**
     * Поле нужно для сохранения состояния текущей сессии по актуальной записи для связывания
     * с текущим актуальным appointment, необходима именно в Tools LLM
     */
    @JsonProperty("unique_id_for_appointment")
    private String uniqueIdForAppointment;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<Appointment> appointments;
}
