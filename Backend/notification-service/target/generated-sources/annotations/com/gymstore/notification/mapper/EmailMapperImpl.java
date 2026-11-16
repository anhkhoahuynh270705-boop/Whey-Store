package com.gymstore.notification.mapper;

import com.gymstore.notification.entity.EmailLog;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-16T13:36:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
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
