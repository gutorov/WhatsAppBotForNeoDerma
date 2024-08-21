package com.ivan_degtev.whatsappbotforneoderma.repository.yClient;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.Appointment;
import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceInformationRepository extends JpaRepository<ServiceInformation, Long> {
//    List<ServiceInformation> findByServiceIdIn(List<Long> serviceIds);
    List<ServiceInformation> findAllByAppointment(Appointment appointment);
    List<ServiceInformation> findAllByAppointment_User(User user);

}
