package com.gymstore.notification.mapper;

import com.gymstore.notification.entity.EmailLog;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-20T09:37:05+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class EmailMapperImpl implements EmailMapper {

    @Override
    public EmailLog toEmailLog(String messageId, String to, String subject, String templateName, String orderId, String userId) {
        if ( messageId == null && to == null && subject == null && templateName == null && orderId == null && userId == null ) {
            return null;
        }

        EmailLog emailLog = new EmailLog();

        emailLog.setMessageId( messageId );
        emailLog.setEmail( to );
        emailLog.setSubject( subject );
        emailLog.setTemplateName( templateName );
        emailLog.setOrderId( orderId );
        emailLog.setUserId( userId );
        emailLog.setStatus( com.gymstore.notification.enums.EmailStatus.PENDING );

        return emailLog;
    }
}
