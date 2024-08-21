package com.ivan_degtev.whatsappbotforneoderma.repository.yClient;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    Appointment findAppointmentByUser(User user);
    List<Appointment> findAllByUser_ChatId(String chatId);
}
