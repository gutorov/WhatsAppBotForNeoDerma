package com.ivan_degtev.whatsappbotforneoderma.repository.yClient;

import com.ivan_degtev.whatsappbotforneoderma.model.yClient.ServiceInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceInformationRepository extends JpaRepository<ServiceInformation, Long> {
    List<ServiceInformation> findByServiceIdIn(List<Long> serviceIds);

}
