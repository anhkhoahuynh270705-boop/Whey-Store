package com.gymstore.gateway.dto.response;

import com.gymstore.gateway.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDto {
    private String   id;
    private String   username;
    private String   email;
    private String   fullName;
    private String   phone;
    private UserRole role;
    private String   status;
    private String   createdAt;
}
