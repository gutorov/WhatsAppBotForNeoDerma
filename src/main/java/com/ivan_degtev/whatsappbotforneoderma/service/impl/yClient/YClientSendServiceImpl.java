package com.ivan_degtev.whatsappbotforneoderma.service.impl.yClient;

import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.tests.AppointmentDto;
import com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.tests.BookingRequestDto;
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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.yclients.v2+json")
//                .defaultHeader("Authorization", "Bearer " + yClientToken)
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
        requestData.put("email", user.getEmail());

        // Сбор данных по встречам
        List<Map<String, Object>> appointmentList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        for (Appointment appointment : appointments) {
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("id", appointment.getId());
            appointmentData.put("staff_id", appointment.getStaffId());
//            appointmentData.put("datetime", appointment.getDatetime());

            OffsetDateTime utcDateTime = appointment.getDatetime();
            OffsetDateTime dateTimeWithOffset = utcDateTime.withOffsetSameInstant(ZoneOffset.ofHours(7));
            String formattedDateTime = dateTimeWithOffset.format(formatter); // Преобразование в нужный формат, включая секунды
            appointmentData.put("datetime", formattedDateTime);

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
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "application/vnd.yclients.v2+json")
                .header("Authorization", "Bearer " + yClientToken)
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
                .doOnError(error -> log.error("Ошибка: {} при отправке запроса: {}", error.getMessage(), requestData.toString()));
    }


    /**
     * Тестовый метод для проверки авторизации и работы записи
     */
    public Mono<ResponseEntity<Map<String, Object>>> sendBookingRequest(BookingRequestDto bookingRequestDto) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("phone", bookingRequestDto.getPhone());
        requestData.put("fullname", bookingRequestDto.getFullname());
        requestData.put("email", bookingRequestDto.getEmail());

        List<Map<String, Object>> appointmentList = new ArrayList<>();
        for (AppointmentDto appointment : bookingRequestDto.getAppointments()) {
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("id", appointment.getId());
            appointmentData.put("services", appointment.getServices());
            appointmentData.put("staff_id", appointment.getStaff_id());
            appointmentData.put("datetime", appointment.getDatetime());
            appointmentList.add(appointmentData);
        }
        requestData.put("appointments", appointmentList);

        String url = String.format("/book_record/%d", companyId);

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "application/vnd.yclients.v2+json")
                .header("Authorization", "Bearer " + yClientToken)
                .bodyValue(requestData)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnSuccess(response -> {
                    Map<String, Object> responseBody = response.getBody();
                    boolean success = responseBody != null && Boolean.TRUE.equals(responseBody.get("success"));
                    if (success) {
                        log.info("Запрос успешно отправлен, ответ: {}", responseBody);
                    } else {
                        log.warn("Запрос не удался, ответ: {}", responseBody);
                    }
                })
                .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()));
    }
}

