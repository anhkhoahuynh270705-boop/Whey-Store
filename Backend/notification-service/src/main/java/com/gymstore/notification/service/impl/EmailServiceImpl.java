package com.gymstore.notification.service.impl;

import com.gymstore.notification.entity.EmailLog;
import com.gymstore.notification.enums.EmailStatus;
import com.gymstore.notification.mapper.EmailMapper;
import com.gymstore.notification.repository.EmailLogRepository;
import com.gymstore.notification.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EmailServiceImpl implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailLogRepository emailLogRepository;
    private final EmailMapper emailMapper;

    @Value("${mail.from:noreply@gymstore.local}")
    private String fromAddress;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine,
            EmailLogRepository emailLogRepository, EmailMapper emailMapper) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailLogRepository = emailLogRepository;
        this.emailMapper = emailMapper;
    }

    @Override
    @Transactional
    public void sendTemplateEmail(String messageId, String to, String subject,
            String templateName, Map<String, Object> variables,
            String orderId, String userId) {
        if (emailLogRepository.existsByMessageId(messageId)) {
            log.info("Email already sent (messageId={}), skipping", messageId);
            return;
        }

        // Build EmailLog via mapper (PENDING state)
        EmailLog logEntry = emailMapper.toEmailLog(messageId, to, subject, templateName, orderId, userId);

        try {
            Context ctx = new Context();
            ctx.setVariables(variables);
            String htmlBody = templateEngine.process(templateName, ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            logEntry.setStatus(EmailStatus.SENT);
            logEntry.setSentAt(LocalDateTime.now());
            log.info("Sent email {} → {} (orderId={})", templateName, to, orderId);
        } catch (MessagingException | RuntimeException ex) {
            log.error("Error sending email (messageId={}): {}", messageId, ex.getMessage());
            logEntry.setStatus(EmailStatus.FAILED);
            logEntry.setErrorMessage(ex.getMessage());
            throw new RuntimeException("Failed to send email: " + ex.getMessage(), ex);
        } finally {
            emailLogRepository.save(logEntry);
        }
    }
}
