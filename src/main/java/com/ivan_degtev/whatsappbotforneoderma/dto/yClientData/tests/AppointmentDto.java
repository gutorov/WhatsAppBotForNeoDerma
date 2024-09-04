package com.ivan_degtev.whatsappbotforneoderma.dto.yClientData.tests;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
public class AppointmentDto {
    private int id;
    private List<Integer> services;
    private int staff_id;
    private OffsetDateTime datetime;
}