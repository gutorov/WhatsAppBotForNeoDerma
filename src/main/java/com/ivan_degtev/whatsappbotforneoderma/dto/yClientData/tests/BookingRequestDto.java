package com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.tests;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BookingRequestDto {
    private String phone;
    private String fullname;
    private String email;
    private List<AppointmentDto> appointments;
}


