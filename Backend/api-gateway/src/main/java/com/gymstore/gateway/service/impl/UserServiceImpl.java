package com.gymstore.gateway.service.impl;

import com.gymstore.gateway.dto.internal.UserAccountData;
import com.gymstore.gateway.dto.request.AdminCreateUserRequest;
import com.gymstore.gateway.dto.request.RegisterRequest;
import com.gymstore.gateway.dto.response.UserAccountDto;
import com.gymstore.gateway.enums.UserRole;
import com.gymstore.gateway.helper.StringHelper;
import com.gymstore.gateway.mapper.UserMapper;
import com.gymstore.gateway.repository.AppUserRepository;
import com.gymstore.gateway.service.KeycloakUserService;
import com.gymstore.gateway.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    KeycloakUserService keycloakService;
    AppUserRepository userRepository;
    UserMapper userMapper;

    @Override
    public UserAccountDto register(RegisterRequest request) {
        String email = StringHelper.normalize(request.getEmail());
        String username = StringHelper.normalize(request.getUsername()).isBlank() ? email
                : StringHelper.normalize(request.getUsername());
        String password = request.getPassword();
        String fullName = StringHelper.normalize(request.getFullName());
        String phone = StringHelper.normalize(request.getPhone());

        if (email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email và mật khẩu không được để trống");
        }

        String userId = keycloakService.createUser(username, email, fullName, phone, password, UserRole.CUSTOMER);

        UserAccountDto dto = userMapper.toDto(
                new UserAccountData(userId, username, email, fullName, phone, UserRole.CUSTOMER, "ACTIVE"));
        userRepository.save(dto);

        log.info("Đã đăng ký tài khoản '{}'", username);
        return dto;
    }

    @Override
    public UserAccountDto adminCreate(AdminCreateUserRequest request) {
        String username = StringHelper.normalize(request.getUsername());
        String email = StringHelper.normalize(request.getEmail());
        String fullName = StringHelper.normalize(request.getFullName());
        String password = (request.getPassword() != null && !request.getPassword().isBlank())
                ? request.getPassword()
                : "123456";
        UserRole role = request.getRole() != null ? request.getRole() : UserRole.STAFF;

        if (username.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException("Username và Email không được để trống");
        }

        String userId = keycloakService.createUser(username, email, fullName, "", password, role);

        UserAccountDto dto = userMapper.toDto(
                new UserAccountData(userId, username, email, fullName, null, role, "ACTIVE"));
        userRepository.save(dto);

        log.info("Admin tạo tài khoản '{}' ({})", username, role.name());
        return dto;
    }

    @Override
    public List<UserAccountDto> listAll() {
        return userRepository.findAll();
    }

    @Override
    public UserAccountDto getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản: " + id));
    }

    @Override
    public UserAccountDto toggleStatus(String id) {
        UserAccountDto user = getById(id);
        String newStatus = "ACTIVE".equalsIgnoreCase(user.getStatus()) ? "DISABLED" : "ACTIVE";

        keycloakService.setUserEnabled(id, "ACTIVE".equalsIgnoreCase(newStatus));
        userRepository.updateStatus(id, newStatus);
        user.setStatus(newStatus);

        log.info("Đổi trạng thái '{}' → '{}'", user.getUsername(), newStatus);
        return user;
    }
}
