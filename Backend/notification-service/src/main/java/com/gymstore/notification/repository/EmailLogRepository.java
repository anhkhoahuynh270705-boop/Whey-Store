package com.gymstore.notification.repository;

import com.gymstore.notification.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    Optional<EmailLog> findByMessageId(String messageId);
    boolean existsByMessageId(String messageId);
}
