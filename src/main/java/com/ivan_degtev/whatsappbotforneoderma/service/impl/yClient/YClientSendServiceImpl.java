package com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Getter
public class YClientSendServiceImpl {

    private final WebClient webClient;

    @Value("${yclient.token}")
    private String yClientToken;

    private final Long companyId = 316398L;

    public YClientSendServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.yclients.com/api/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + yClientToken)
                .build();
    }

    public Mono<ResponseEntity<Map<String, Object>>> sendBookingRequest(
            User user,
            List<Appointment> appointments,
            List<ServiceInformation> serviceInfoList
    ) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("phone", user.getSenderPhoneNumber());
        requestData.put("fullname", user.getSenderName());

        // Сбор данных по встречам
        List<Map<String, Object>> appointmentList = new ArrayList<>();
        for (Appointment appointment : appointments) {
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("id", appointment.getId());
            appointmentData.put("staff_id", appointment.getStaffId());
            appointmentData.put("datetime", appointment.getDatetime());

            // Получение соответствующей информации о сервисах для встречи
            List<Integer> services = serviceInfoList.stream()
                    .filter(serviceInfo -> serviceInfo.getAppointment().getId().equals(appointment.getId()))
                    .map(serviceInfo -> Integer.valueOf(serviceInfo.getServiceId()))
                    .collect(Collectors.toList());
            appointmentData.put("services", services);

            appointmentList.add(appointmentData);
        }
        requestData.put("appointments", appointmentList);

        String url = String.format("/book_record/%d", companyId);

        return webClient.post()
                .uri(url)
                .bodyValue(requestData)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnSuccess(response -> {
                    Map<String, Object> responseBody = response.getBody();
                    boolean success = (Boolean) responseBody.get("success");
                    if (success) {
                        log.info("Запрос успешно отправлен, ответ: {}", responseBody);
                    } else {
                        log.warn("Запрос не удался, ответ: {}", responseBody);
                    }
                })
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()));
    }
}

