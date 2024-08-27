package com.ivan_degtev.whatsappbotforneoderma.service.util;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import com.ivan_degtev.whatsappbotforneoderma.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
//@Scope("prototype")
public class UserChecks {

    private final  UserRepository userRepository;
    private final JsonLoggingService jsonLogging;

    /**
     * Метод добавляет уникальный номер для юзера, для дальнейшей связи между юзером и объектами записи на сеанс
     * ПРоисходит это лишь при первом сообщении(добавление нового юззера в базу)
     * или
     * если все связанные сущности сеанса у юзера финализированы(завершены) и это значит, что юзер хочет создать новую
     * запись на сеас
     * @param currentUser
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User addingUniqueIdForAppointmentIsNone(User currentUser) {
        if (currentUser.getUniqueIdForAppointment() == null) {
            currentUser.setUniqueIdForAppointment(UUID.randomUUID().toString());
            userRepository.save(currentUser);
            log.info("Сохранил уникальный уид для нового юзера {}", currentUser.toString());
            return currentUser;
        } else if (!currentUser.getAppointments().isEmpty() &&
                currentUser.getAppointments()
                .stream()
                .allMatch(appointment ->
                        Boolean.TRUE.equals(appointment.getCompletedBooking()) &&
                                Boolean.TRUE.equals(appointment.getApplicationSent())))
        {
            currentUser.setUniqueIdForAppointment(UUID.randomUUID().toString());
            userRepository.save(currentUser);
            log.info("Сохранил уникальный уид для уже не нового юзера {}", currentUser.toString());
            return currentUser;
        }
        jsonLogging.info("Возвращаю из метода addingUniqueIdForAppointmentIsNone текущего юзера с изменениями {}",
                currentUser.toString());
        return currentUser;
    }

    /**
     * Утилитный метод проверяет есть ли юзер по чат-айди в БД, для понимания были ли с ним ранее диалоги. При отсутсвии -
     * юзер добавляется в БД, при наличии - добавляются только новые сообщения, связываюсь с текущим юзером по чат-айди
     */
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public boolean checkingExistenceUserByChatId(String currentChatId) {
        return userRepository.existsByChatId(currentChatId);
    }

}
