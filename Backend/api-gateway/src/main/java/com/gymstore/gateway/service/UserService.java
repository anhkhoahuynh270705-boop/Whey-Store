package com.gymstore.gateway.service;

import com.gymstore.gateway.dto.request.AdminCreateUserRequest;
import com.gymstore.gateway.dto.request.RegisterRequest;
import com.gymstore.gateway.dto.response.UserAccountDto;

import java.util.List;

public interface UserService {

    UserAccountDto register(RegisterRequest request);

    UserAccountDto adminCreate(AdminCreateUserRequest request);

    List<UserAccountDto> listAll();

    UserAccountDto getById(String id);

    UserAccountDto toggleStatus(String id);
}
