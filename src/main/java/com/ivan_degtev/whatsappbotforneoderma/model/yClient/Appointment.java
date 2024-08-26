package com.ivan_degtev.whatsappbotforneoderma.model.yClient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.whatsappbotforneoderma.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = { "user" } )
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("services_information")
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    @JsonIgnore
    private List<ServiceInformation> servicesInformation;

    @JsonProperty("staff_id")
    private String staffId;

    @JsonProperty("datetime")
    private OffsetDateTime datetime;

    /**
     * Поле показыывает полностью заполненный appointment  и готовый для отправки через post-запрос
     * Проствавляется в финальном Tool, чекается в сервисе langchain4j
     */
    @JsonProperty("completed_booking")
    private Boolean completedBooking;

    /**
     * Финальный флаг, значащий, что данные были успешно отправлены на YClient и этот appointment завершён и в дальнейшем
     * не должен быть использован.
     */
    @JsonProperty("application_sent")
    private Boolean applicationSent;
    /**
     * Поле нужно для сохранения состояния текущей сессии по актуальной записи для связывания
     * с текущим актуальным appointment, необходима именно в Tools LLM
     */
    @JsonProperty("unique_id_for_appointment")
    private String uniqueIdForAppointment;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private User user;
}
