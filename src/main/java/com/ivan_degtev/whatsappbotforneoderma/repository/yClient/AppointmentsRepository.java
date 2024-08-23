package com.ivan_degtev.whatsappbotforneoderma.repository.yClient;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    Appointment findAppointmentByUser(User user);
    List<Appointment> findAllByUser_ChatId(String chatId);

    /**
     * Получить конкретный апойтмент для текущей заявки на бронь сеанса
     * @param uniqueId
     * @return актуальный Appointment
     */
    Optional<Appointment> findByUser_UniqueIdForAppointment(String uniqueId);
}
