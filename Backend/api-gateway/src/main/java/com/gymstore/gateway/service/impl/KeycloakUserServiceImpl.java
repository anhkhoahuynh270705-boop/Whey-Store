package com.gymstore.gateway.service.impl;

import com.gymstore.gateway.config.KeycloakConfig;
import com.gymstore.gateway.enums.UserRole;
import com.gymstore.gateway.helper.HttpHelper;
import com.gymstore.gateway.helper.KeycloakUserPayloadFactory;
import com.gymstore.gateway.helper.StringHelper;
import com.gymstore.gateway.service.KeycloakUserService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KeycloakUserServiceImpl implements KeycloakUserService {

    KeycloakConfig kc;
    HttpHelper http;

    @Override
    public String createUser(String username, String email,
            String fullName, String phone,
            String password, UserRole role) {
        try {
            String token = getAdminToken();

            if (getUserId(token, username) != null) {
                throw new IllegalStateException("Tài khoản '" + username + "' đã tồn tại");
            }

            String[] names = StringHelper.splitFullName(fullName);
            String body = KeycloakUserPayloadFactory.toCreateUserJson(
                    username, email, names[0], names[1], phone, password);

            HttpResponse<String> resp = http.postJson(kc.usersUrl(), token, body);
            if (resp.statusCode() != 201 && resp.statusCode() != 200) {
                throw new IllegalStateException(
                        "Keycloak tạo user thất bại: HTTP " + resp.statusCode() + " " + resp.body());
            }

            String userId = getUserId(token, username);
            if (userId == null) {
                userId = UUID.randomUUID().toString();
            }

            assignRole(token, userId, role);

            // Gửi email xác minh sau khi tạo thành công
            sendVerificationEmail(token, userId, email);

            return userId;

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("createUser '{}': {}", username, e.getMessage(), e);
            throw new RuntimeException("Lỗi tạo tài khoản trên Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendVerificationEmail(String adminToken, String userId, String email) {
        try {
    
            http.putJson(kc.userUrl(userId), adminToken,
                    "{\"emailVerified\": false}");
            String verifyUrl = kc.sendVerifyEmailUrl(userId)
                    + "?client_id=" + kc.getClientId()
                    + "&redirect_uri=" + URLEncoder.encode(
                            "http://localhost:4200/home", StandardCharsets.UTF_8);

            HttpResponse<String> resp = http.putJson(verifyUrl, adminToken, "");
            if (resp.statusCode() == 204 || resp.statusCode() == 200) {
                log.info("Đã gửi email xác minh cho user '{}' ({})", userId, email);
            } else {
                log.warn("Gửi email xác minh thất bại: HTTP {} - {}", resp.statusCode(), resp.body());
            }
        } catch (Exception e) {
            log.warn("Không thể gửi email xác minh cho '{}': {}", email, e.getMessage());
        }
    }

    @Override
    public void setUserEnabled(String userId, boolean enabled) {
        try {
            String token = getAdminToken();
            http.putJson(kc.userUrl(userId), token,
                    String.format("{\"enabled\": %b}", enabled));
        } catch (Exception e) {
            log.warn("setUserEnabled id='{}': {}", userId, e.getMessage());
        }
    }

    @Override
    public void revokeSession(String refreshToken) {
        try {
            String encoded = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

            http.postForm(kc.revokeUrl(),
                    "client_id=" + kc.getClientId()
                    + "&token=" + encoded
                    + "&token_type_hint=refresh_token");

            http.postForm(kc.logoutUrl(),
                    "client_id=" + kc.getClientId()
                    + "&refresh_token=" + encoded);

            log.info("Session revoked for client '{}'", kc.getClientId());
        } catch (Exception e) {
            log.warn("revokeSession: {}", e.getMessage());
        }
    }

    /**
     * Kiểm tra Keycloak có sẵn sàng không.
     */
    @Override
    public boolean isAvailable() {
        try {
            getAdminToken();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Keycloak-specific helpers
    String getAdminToken() throws Exception {
        HttpResponse<String> resp = http.postForm(kc.adminTokenUrl(), kc.adminTokenForm());
        if (resp.statusCode() != 200) {
            throw new IllegalStateException("Không lấy được admin token: HTTP " + resp.statusCode());
        }
        Matcher m = Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\"").matcher(resp.body());
        if (m.find()) {
            return m.group(1);
        }
        throw new IllegalStateException("Không tìm thấy access_token trong response");
    }

    String getUserId(String token, String username) throws Exception {
        String url = kc.usersUrl()
                + "?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                + "&exact=true";
        HttpResponse<String> resp = http.get(url, token);
        if (resp.statusCode() == 200) {
            Matcher m = Pattern.compile("\"id\"\\s*:\\s*\"([a-f0-9\\-]+)\"").matcher(resp.body());
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    private void assignRole(String token, String userId, UserRole role) throws Exception {
        String roleName = role.name();
        HttpResponse<String> roleResp = http.get(kc.roleUrl(roleName), token);
        if (roleResp.statusCode() != 200) {
            log.warn("Role '{}' không tồn tại trên Keycloak", roleName);
            return;
        }
        HttpResponse<String> mapResp = http.postJson(
                kc.userRoleMappingsUrl(userId), token, "[" + roleResp.body() + "]");
        log.info("Gán role '{}' cho user '{}': HTTP {}", roleName, userId, mapResp.statusCode());
    }
}
