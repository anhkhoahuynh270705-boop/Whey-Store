package com.gymstore.gateway.mapper;

import com.gymstore.gateway.dto.internal.UserAccountData;
import com.gymstore.gateway.dto.response.UserAccountDto;
import com.gymstore.gateway.dto.response.UserAccountRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapperSupport.class)
public interface UserMapper {

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "status", defaultValue = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(com.gymstore.gateway.mapper.UserMapperSupport.formatNow())")
    UserAccountDto toDto(UserAccountData user);

    @Mapping(target = "role", source = "role", qualifiedByName = "toUserRole")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatCreatedAt")
    UserAccountDto fromRow(UserAccountRow row);
}
