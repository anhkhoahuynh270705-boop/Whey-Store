package com.gymstore.gateway.mapper;

import com.gymstore.gateway.dto.internal.UserAccountData;
import com.gymstore.gateway.dto.response.UserAccountDto;
import com.gymstore.gateway.dto.response.UserAccountRow;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-20T11:18:13+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserAccountDto toDto(UserAccountData user) {
        if ( user == null ) {
            return null;
        }

        UserAccountDto.UserAccountDtoBuilder userAccountDto = UserAccountDto.builder();

        userAccountDto.id( user.userId() );
        if ( user.status() != null ) {
            userAccountDto.status( user.status() );
        }
        else {
            userAccountDto.status( "ACTIVE" );
        }
        userAccountDto.username( user.username() );
        userAccountDto.email( user.email() );
        userAccountDto.fullName( user.fullName() );
        userAccountDto.phone( user.phone() );
        userAccountDto.role( user.role() );

        userAccountDto.createdAt( com.gymstore.gateway.mapper.UserMapperSupport.formatNow() );

        return userAccountDto.build();
    }

    @Override
    public UserAccountDto fromRow(UserAccountRow row) {
        if ( row == null ) {
            return null;
        }

        UserAccountDto.UserAccountDtoBuilder userAccountDto = UserAccountDto.builder();

        userAccountDto.role( UserMapperSupport.toUserRole( row.role() ) );
        userAccountDto.createdAt( UserMapperSupport.formatCreatedAt( row.createdAt() ) );
        userAccountDto.id( row.id() );
        userAccountDto.username( row.username() );
        userAccountDto.email( row.email() );
        userAccountDto.fullName( row.fullName() );
        userAccountDto.phone( row.phone() );
        userAccountDto.status( row.status() );

        return userAccountDto.build();
    }
}
