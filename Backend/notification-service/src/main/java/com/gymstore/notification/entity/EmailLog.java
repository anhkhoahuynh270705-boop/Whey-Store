package com.gymstore.notification.entity;

import com.gymstore.notification.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** Ghi nhật ký mỗi email → đảm bảo idempotent consumer (không gửi trùng). */
@Entity
@Table(name = "email_logs",
        indexes = @Index(name = "idx_email_log_message_id", columnList = "message_id", unique = true))
@Getter
@Setter
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** messageId = Kafka key (orderId). Unique để chống duplicate. */
    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "user_id")
    private String userId;

    private String email;
    private String subject;

    @Column(name = "template_name")
    private String templateName;

    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    @Column(name = "retry_count")
    private int retryCount;

    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
