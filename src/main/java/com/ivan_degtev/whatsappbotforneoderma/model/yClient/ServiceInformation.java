package com.ivan_degtev.whatsappbotforneoderma.model.yClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "service_information")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = { "appointment" } )
public class ServiceInformation {

    public ServiceInformation(String serviceId) {
        this.serviceId = serviceId;
    }
    public ServiceInformation(String serviceId, String title) {
        this.serviceId = serviceId;
        this.title = title;
    }
    public ServiceInformation(String serviceId, String title, String priceMin, String priceMax) {
        this.serviceId = serviceId;
        this.title = title;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("service_id")
    private String serviceId;

    private String title;

    @JsonProperty("price_min")
    private String priceMin;

    @JsonProperty("price_max")
    private String priceMax;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private Appointment appointment;
}
