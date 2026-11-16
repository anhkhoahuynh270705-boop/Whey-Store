package com.gymstore.notification.service;

import java.util.Map;

/**
 * Interface for email sending service.
 * Sends HTML emails using Thymeleaf templates + Spring Mail (Mailhog for local).
 * Each email is logged in email_logs with unique messageId for idempotency.
 */
public interface IEmailService {

    /**
     * Send email from Thymeleaf template.
     * 
     * @param messageId    Kafka message key (used as idempotent guard)
     * @param to           recipient email address
     * @param subject      email subject
     * @param templateName Thymeleaf template name (without .html extension)
     * @param variables    variables to pass to template
     * @param orderId      order ID for logging
     * @param userId       user ID for logging
     * @throws RuntimeException if email sending fails
     */
    void sendTemplateEmail(String messageId, String to, String subject,
                          String templateName, Map<String, Object> variables,
                          String orderId, String userId);
}
