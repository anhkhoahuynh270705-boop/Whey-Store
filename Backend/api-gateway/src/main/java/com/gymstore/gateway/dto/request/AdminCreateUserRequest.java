package com.gymstore.gateway.dto.request;

import com.gymstore.gateway.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateUserRequest {

    private String username;
    private String email;
    private String password;
    private String fullName;
    private UserRole role;
}
