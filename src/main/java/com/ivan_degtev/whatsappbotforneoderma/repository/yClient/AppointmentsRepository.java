package com.ivan_degtev.whatsappbotforneoderma.repository.yClient;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    Appointment findAppointmentByUser(User user);
}
