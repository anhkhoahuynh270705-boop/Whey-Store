package com.gymstore.notification.mapper;

import com.gymstore.notification.entity.EmailLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    @Mapping(target = "email", source = "to")
    @Mapping(target = "status", expression = "java(com.gymstore.notification.enums.EmailStatus.PENDING)")
    EmailLog toEmailLog(String messageId, String to, String subject,
            String templateName, String orderId, String userId);
}
