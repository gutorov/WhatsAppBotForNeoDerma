package com.ivan_degtev.whatsappbotforneoderma.repository;

import com.ivan_degtev.whatsappbotforneoderma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByChatId(String chatId);
    boolean existsByChatId(String chatId);
}
